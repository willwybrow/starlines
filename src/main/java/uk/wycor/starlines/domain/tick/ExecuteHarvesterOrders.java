package uk.wycor.starlines.domain.tick;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.OrderExecutingService;
import uk.wycor.starlines.domain.order.Harvest;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;

import java.time.Instant;
import java.util.function.Predicate;

@Component
@org.springframework.core.annotation.Order(1)
public class ExecuteHarvesterOrders extends ExecuteOrders {
    private final HarvesterRepository harvesterRepository;
    private final OrderExecutingService orderExecutingService;

    public ExecuteHarvesterOrders(OrderRepository orderRepository, HarvesterRepository harvesterRepository, OrderExecutingService orderExecutingService) {
        super(orderRepository);
        this.harvesterRepository = harvesterRepository;
        this.orderExecutingService = orderExecutingService;
    }

    @Override
    public Flux<Order> executeOrders(Instant thisTick, Instant nextTick) {
        return executeHarvestOrders(thisTick).map(harvest -> harvest);
    }

    private Predicate<uk.wycor.starlines.domain.order.Order> canExecuteOrder(Instant onThisTick) {
        return order -> (order.getExecutedAt() == null || order.getExecutedAt().isBefore(onThisTick)) && order.getScheduledFor().equals(onThisTick);
    }

    @Neo4jTransactional
    private Flux<Harvest> executeHarvestOrders(Instant forTick) {
        return harvesterRepository
                .findAll()
                .flatMap(harvester -> Mono
                        .justOrEmpty(harvester.getOrdersToHarvest().stream().filter(canExecuteOrder(forTick)).findFirst())
                        .flatMap(harvest -> orderExecutingService.harvest(forTick, harvest, harvester))
                );
    }
}
