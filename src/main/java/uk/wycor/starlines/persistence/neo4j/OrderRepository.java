package uk.wycor.starlines.persistence.neo4j;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import uk.wycor.starlines.domain.order.Order;

import java.util.UUID;

public interface OrderRepository extends ReactiveNeo4jRepository<Order, UUID> {
}
