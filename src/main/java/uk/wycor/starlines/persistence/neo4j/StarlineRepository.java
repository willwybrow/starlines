package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.support.ReactiveCypherdslStatementExecutor;
import org.springframework.stereotype.Repository;
import uk.wycor.starlines.domain.Starline;

import java.util.UUID;

@Repository
public interface StarlineRepository extends ReactiveNeo4jRepository<Starline, UUID>, ReactiveCypherdslStatementExecutor<Starline> {
}
