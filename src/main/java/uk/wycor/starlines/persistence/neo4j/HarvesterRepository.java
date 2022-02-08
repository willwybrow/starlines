package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import uk.wycor.starlines.domain.ship.Harvester;

import java.util.UUID;

public interface HarvesterRepository extends ReactiveNeo4jRepository<Harvester, UUID> {
}
