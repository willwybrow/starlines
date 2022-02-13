package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import dev.wycobar.starlines.domain.Starline;

import java.util.UUID;

@Repository
public interface StarlineRepository extends ReactiveNeo4jRepository<Starline, UUID> {
}
