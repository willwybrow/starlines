package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import uk.wycor.starlines.domain.ship.Stabiliser;

import java.util.UUID;

public interface StabiliserRepository extends ReactiveNeo4jRepository<Stabiliser, UUID> {
}
