package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import dev.wycobar.starlines.domain.ship.Harvester;

import java.util.UUID;

public interface HarvesterRepository extends ReactiveNeo4jRepository<Harvester, UUID> {
}
