package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.star.Cluster;
import uk.wycor.starlines.domain.star.ClusterID;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.domain.star.StarfieldGenerator;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UniverseService {
    private final static Logger logger = Logger.getLogger(UniverseService.class.getName());

    private final StarRepository starRepository;

    @Autowired
    public UniverseService(StarRepository starRepository) {
        this.starRepository = starRepository;
    }

    @Neo4jTransactional
    public Mono<Cluster> expandUniverse() {
        return starRepository
                .findFirstByOrderByClusterIDDesc()
                .doOnNext(latestStar -> logger.info(String.format("Star with latest cluster ID is %s (%s) at %d", latestStar.getName(), latestStar.getId().toString(), latestStar.getClusterNumber())))
                .map(Star::getClusterID)
                .map(ClusterID::getNumeric)
                .map(clusterNumber -> clusterNumber + 1)
                .map(ClusterID::new)
                .switchIfEmpty(Mono.defer(() -> Mono.fromSupplier(() -> new ClusterID(0))))
                .flatMap(this::generateClusterAt);
    }

    @Neo4jTransactional
    public Mono<Cluster> getCluster(ClusterID clusterID, StarlinesGame starlinesGame) {
        return starRepository.findByClusterIDEquals(clusterID)
                .doOnNext(star -> logger.info(String.format("Found star %s (%s) from db lookup ", star.getName(), star.getId().toString())))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info(String.format("Cluster %d was empty???", clusterID.getNumeric()));
                    return generateClusterAt(clusterID);
                }).flatMapMany(cluster -> Flux.fromIterable(cluster.getStars())))
                .collect(Collectors.toSet())
                .map(stars -> new Cluster(clusterID.withNeighbours(), stars));
    }

    public Flux<Cluster> getClusters(Set<ClusterID> clusterIDs, StarlinesGame starlinesGame) {
        return starRepository.findByClusterIDIn(clusterIDs)
                .collect(Collectors.groupingBy(Star::getClusterID, Collectors.toSet()))
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> new Cluster(entry.getKey().withNeighbours(), entry.getValue()));
    }

    private Mono<Cluster> generateClusterAt(ClusterID clusterID) {
        logger.info(String.format("Called to generate cluster at %d", clusterID.getNumeric()));
        return Mono.fromSupplier(() -> clusterID)
                .map(StarfieldGenerator::generateRandomStarfield)
                .doOnNext(stars -> logger.info(String.format("Generated %d new stars", stars.size())))
                .flatMapMany(starRepository::saveAll)
                .doOnNext(star -> logger.info(String.format("Saved star %s into cluster %d", star.getId().toString(), star.getClusterNumber())))
                .collect(Collectors.toSet())
                .doOnNext(stars -> logger.info(String.format("Saved %d new stars", stars.size())))
                .map(stars -> new Cluster(clusterID, stars));
    }
}
