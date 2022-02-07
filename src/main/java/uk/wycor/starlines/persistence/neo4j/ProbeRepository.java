package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import uk.wycor.starlines.domain.Probe;

import java.util.UUID;

public interface ProbeRepository extends ReactiveNeo4jRepository<Probe, UUID> {
}
