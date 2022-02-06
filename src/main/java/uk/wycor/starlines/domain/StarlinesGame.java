package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.persistence.neo4j.StarlineRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StarlinesGame {
    private final static Logger logger = Logger.getLogger(StarlinesGame.class.getName());

    private final Clock clock;
    private final StarRepository starRepository;
    private final PlayerRepository playerRepository;
    private final StarlineRepository starlineRepository;

    @Autowired
    public StarlinesGame(Clock clock, StarRepository starRepository, PlayerRepository playerRepository, StarlineRepository starlineRepository) {
        this.clock = clock;
        this.starRepository = starRepository;
        this.playerRepository = playerRepository;
        this.starlineRepository = starlineRepository;
    }

    public Mono<Cluster> getCluster(ClusterID clusterID) {
        return starRepository.findByClusterIDEquals(clusterID)
                .collect(Collectors.toSet())
                .map(stars -> new Cluster(clusterID.withNeighbours(), stars));
    }

    public Flux<Cluster> getClusters(Set<ClusterID> clusterIDs) {
        return starRepository.findByClusterIDIn(clusterIDs)
                .collect(Collectors.groupingBy(Star::getClusterID, Collectors.toSet()))
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> new Cluster(entry.getKey().withNeighbours(), entry.getValue()));
    }

    public Mono<ClusterID> getMostRecentlyGeneratedCluster() {
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
