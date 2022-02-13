package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import dev.wycobar.starlines.domain.GameState;

import java.util.UUID;

public interface GameStateRepository extends ReactiveNeo4jRepository<GameState, UUID> {

}
