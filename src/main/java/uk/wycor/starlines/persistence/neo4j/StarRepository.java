package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Star;

import java.util.Set;
import java.util.UUID;

@Repository
public interface StarRepository extends ReactiveNeo4jRepository<Star, UUID> {
    Flux<Star> findByClusterIDEquals(ClusterID clusterID);

    Flux<Star> findByClusterIDIn(Set<ClusterID> clusterIDs);

    Mono<Star> findFirstByOrderByClusterIDDesc();
}
