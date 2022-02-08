package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import uk.wycor.starlines.domain.GameState;

import java.util.UUID;

public interface GameStateRepository extends ReactiveNeo4jRepository<GameState, UUID> {

}
