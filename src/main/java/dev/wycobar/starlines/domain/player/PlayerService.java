package dev.wycobar.starlines.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.UniverseService;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.domain.star.Cluster;
import dev.wycobar.starlines.domain.star.ClusterID;
import dev.wycobar.starlines.domain.star.Star;
import dev.wycobar.starlines.persistence.neo4j.Neo4jTransactional;
import dev.wycobar.starlines.persistence.neo4j.PlayerRepository;
import dev.wycobar.starlines.persistence.neo4j.ProbeRepository;
import dev.wycobar.starlines.persistence.neo4j.StarRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PlayerService {
    private static final Logger logger = Logger.getLogger(PlayerService.class.getName());

    private final PlayerRepository playerRepository;
    private final StarRepository starRepository;
    private final ProbeRepository probeRepository;
    private final UniverseService universeService;

    @Autowired
    public PlayerService(
            PlayerRepository playerRepository,
            StarRepository starRepository,
            ProbeRepository probeRepository,
            UniverseService universeService) {
        this.playerRepository = playerRepository;
        this.starRepository = starRepository;
        this.probeRepository = probeRepository;
        this.universeService = universeService;
    }

    @Neo4jTransactional
    public Mono<Player> loadOrCreatePlayer(Player player) {
        return playerRepository
                .findById(player.getId())
                .switchIfEmpty(Mono.defer(() -> setUpNewPlayer(player)));
    }

    private Mono<Player> setUpNewPlayer(Player player) {
        logger.info(String.format("Player %s (%s) doesn't exist!", player.getName(), player.getId().toString()));
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
                .then(Mono.defer(() -> Mono.just(player)));
    }

    private Star bestStar(Collection<Star> stars) {
        return Collections.max(stars, Comparator.comparingLong(Star::getNaturalMassCapacity));
    }

    private Set<Probe> buildInitialProbes(Player owner, Star orbiting) {
        logger.info(String.format("Generating new probes for player %s (%s)", owner.getName(), owner.getId().toString()));
        return IntStream.range(0, 5).mapToObj(i -> new Probe(UUID.randomUUID(), owner, orbiting)).collect(Collectors.toSet());
    }

    private Mono<ClusterID> getMostRecentlyGeneratedCluster() {
        return starRepository.findFirstByOrderByClusterIDDesc().map(Star::getClusterID);
    }
}
