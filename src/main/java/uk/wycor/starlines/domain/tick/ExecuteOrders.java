package uk.wycor.starlines.domain.tick;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.ship.order.Order;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;

import java.time.Instant;
import java.util.function.Predicate;

public abstract class ExecuteOrders<T extends Order> implements OnTickAction {
    protected OrderRepository orderRepository;

    public ExecuteOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public final Flux<Void> processActions(Instant thisTick, Instant nextTick) {
        return executeAllOrdersAndRefreshRepeatable(thisTick, nextTick);
    }

    abstract Flux<T> executeOrders(Instant thisTick, Instant nextTick);

    Predicate<Order> canExecuteOrder(Instant onThisTick) {
        return order -> (order.getExecutedAt() == null || order.getExecutedAt().isBefore(onThisTick)) && order.getScheduledFor().equals(onThisTick);
    }

    private Flux<Void> executeAllOrdersAndRefreshRepeatable(Instant thisTick, Instant nextTick) {
        return executeOrders(thisTick, nextTick)
                .flatMap(order -> {
                    if (order.isRepeatable()) {
                        order.setScheduledFor(nextTick);
                        return orderRepository.save(order).then();
                    }
                    return Mono.empty();
                });
    }
}
