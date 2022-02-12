package uk.wycor.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;

import java.time.Instant;

public abstract class ExecuteOrders implements OnTickAction {
    protected OrderRepository orderRepository;

    @Autowired
    public ExecuteOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public final Flux<Void> processActions(Instant thisTick, Instant nextTick) {
        return executeAllOrdersAndRefreshRepeatable(thisTick, nextTick);
    }

    abstract Flux<Order> executeOrders(Instant thisTick, Instant nextTick);

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
