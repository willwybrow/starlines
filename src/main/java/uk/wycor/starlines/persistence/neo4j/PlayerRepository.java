package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import uk.wycor.starlines.domain.Player;

import java.util.UUID;

@Repository
public interface PlayerRepository extends ReactiveNeo4jRepository<Player, UUID> {
}
