package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.support.ReactiveCypherdslStatementExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import uk.wycor.starlines.domain.Star;

import java.util.UUID;

@Repository
public interface StarRepository extends ReactiveNeo4jRepository<Star, UUID>, ReactiveCypherdslStatementExecutor<Star> {
    @Query("MATCH (star) WHERE star.clusterID = $clusterNumber RETURN star")
    Flux<Star> getInCluster(@Param("clusterNumber") Long clusterNumber);
}
