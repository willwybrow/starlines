package dev.wycobar.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import dev.wycobar.starlines.domain.ship.order.Order;

import java.util.UUID;

public interface OrderRepository extends ReactiveNeo4jRepository<Order, UUID> {
}
