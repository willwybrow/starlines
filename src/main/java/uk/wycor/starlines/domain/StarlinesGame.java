package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.persistence.neo4j.StarlineRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StarlinesGame {
    private final static Logger logger = Logger.getLogger(StarlinesGame.class.getName());

    private final Clock clock;
    private final UniverseService universeService;
    private final StarRepository starRepository;
    private final PlayerRepository playerRepository;
    private final StarlineRepository starlineRepository;
    private final ProbeRepository probeRepository;

    @Autowired
    public StarlinesGame(Clock clock, UniverseService universeService, StarRepository starRepository, PlayerRepository playerRepository, StarlineRepository starlineRepository, ProbeRepository probeRepository) {
        this.clock = clock;
        this.universeService = universeService;
        this.starRepository = starRepository;
        this.playerRepository = playerRepository;
        this.starlineRepository = starlineRepository;
        this.probeRepository = probeRepository;
    }

    public Mono<Cluster> getCluster(ClusterID clusterID) {
        return starRepository.findByClusterIDEquals(clusterID)
                .doOnNext(star -> logger.info(String.format("Found star %s (%s) from db lookup ", star.getName(), star.getId().toString())))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info(String.format("Cluster %d was empty???", clusterID.getNumeric()));
                    return universeService.generateClusterAt(clusterID);
                }).flatMapMany(cluster -> Flux.fromIterable(cluster.getStars())))
                .collect(Collectors.toSet())
                .map(stars -> new Cluster(clusterID.withNeighbours(), stars));
    }

    public Flux<Cluster> getClusters(Set<ClusterID> clusterIDs) {
        return starRepository.findByClusterIDIn(clusterIDs)
                .collect(Collectors.groupingBy(Star::getClusterID, Collectors.toSet()))
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> new Cluster(entry.getKey().withNeighbours(), entry.getValue()));
    }

    @Neo4jTransactional
    public Mono<Player> loadOrCreatePlayer(Player player) {
        return playerRepository
                .findById(player.getId())
                .switchIfEmpty(Mono.defer(() -> setUpNewPlayer(player)));
    }

    private Mono<Player> setUpNewPlayer(Player player) {
        return universeService
                .expandUniverse()
                .flatMap(cluster -> populateCluster(cluster, player));
    }

    private Mono<Player> populateCluster(Cluster cluster, Player player) {
        return Mono.fromSupplier(() -> cluster)
                .map(Cluster::getStars)
                .map(this::bestStar)
                .doOnNext(bs -> logger.info(String.format("Picked best star %s (%s)", bs.getName(), bs.getId().toString())))
                .map(bestStar -> buildInitialProbes(player, bestStar))
                .flatMapMany(Flux::fromIterable)
                .doOnNext(probe -> logger.info(String.format("Saving probe %s orbiting star %s (%s) in cluster %d", probe.getId().toString(), probe.getOrbiting().getName(), probe.getOrbiting().getId().toString(), probe.getOrbiting().getClusterNumber())))
                .flatMap(probeRepository::save)
                .then(Mono.fromSupplier(() -> player));
    }

    private Star bestStar(Collection<Star> stars) {
        return Collections.max(stars, Comparator.comparingLong(Star::getNaturalMassCapacity));
    }

    private Set<Probe> buildInitialProbes(Player owner, Star orbiting) {
        logger.info("Generating new Probe for player " + owner.getName());
        return IntStream.range(0, 5).mapToObj(i -> new Probe(UUID.randomUUID(), owner, orbiting)).collect(Collectors.toSet());
    }

    private Mono<ClusterID> getMostRecentlyGeneratedCluster() {
        return starRepository.findFirstByOrderByClusterIDDesc().map(Star::getClusterID);
    }

    public Instant nextTick() {
        LocalDateTime now = LocalDateTime.ofInstant(this.clock.instant(), ZoneOffset.UTC);
        LocalDateTime startOfThisHour = now.truncatedTo(ChronoUnit.HOURS);
        return IntStream.range(0, 5)
                .map(i -> i * 15)
                .mapToObj(startOfThisHour::plusMinutes)
                .filter(time -> time.isAfter(now))
                .findFirst()
                .orElseThrow(RuntimeException::new) // TODO: something better
                .toInstant(ZoneOffset.UTC);
    }

}
