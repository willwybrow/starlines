package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.support.ReactiveCypherdslStatementExecutor;
import org.springframework.stereotype.Repository;
import uk.wycor.starlines.domain.StarlineLeg;

@Repository
public interface StarlineSpanRepository extends ReactiveNeo4jRepository<StarlineLeg, Long>, ReactiveCypherdslStatementExecutor<StarlineLeg> {
}
