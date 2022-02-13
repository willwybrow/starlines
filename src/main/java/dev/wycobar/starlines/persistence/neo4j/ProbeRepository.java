package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import dev.wycobar.starlines.domain.ship.Probe;

import java.util.UUID;

public interface ProbeRepository extends ReactiveNeo4jRepository<Probe, UUID> {
//    @Query("""
//            MATCH (player:Player) <-[o:OWNED_BY]- (probe:Probe) -[orb:ORBITING]->(star:Star) \s
//            WHERE player.id = $playerID
//            RETURN player, o, probe, orb, star;
//            """)
//    Flux<Probe> getMyProbes(UUID playerID);
}
