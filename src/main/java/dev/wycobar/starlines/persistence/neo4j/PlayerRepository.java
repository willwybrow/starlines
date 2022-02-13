package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import dev.wycobar.starlines.domain.player.Player;

import java.util.UUID;

@Repository
public interface PlayerRepository extends ReactiveNeo4jRepository<Player, UUID> {
}
