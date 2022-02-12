package uk.wycor.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.OrderExecutingService;
import uk.wycor.starlines.domain.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StabiliserRepository;

import java.time.Instant;
import java.util.function.Predicate;

@Component
@org.springframework.core.annotation.Order(1)
public class ExecuteProbeOrders extends ExecuteOrders {
    private final ProbeRepository probeRepository;
    private final OrderExecutingService orderExecutingService;

    @Autowired
    public ExecuteProbeOrders(OrderRepository orderRepository, ProbeRepository probeRepository, HarvesterRepository harvesterRepository, StabiliserRepository stabiliserRepository, OrderExecutingService orderExecutingService) {
        super(orderRepository);
        this.probeRepository = probeRepository;
        this.orderExecutingService = orderExecutingService;
    }

    @Override
    public Flux<Order> executeOrders(Instant thisTick, Instant nextTick) {
        return executeProbeEstablishmentOrders(thisTick)
                .map(establishSelfAsHarvester -> establishSelfAsHarvester);
    }

    private Predicate<Order> canExecuteOrder(Instant onThisTick) {
        return order -> (order.getExecutedAt() == null || order.getExecutedAt().isBefore(onThisTick)) && order.getScheduledFor().equals(onThisTick);
    }

    @Neo4jTransactional
    private Flux<EstablishSelfAsHarvester> executeProbeEstablishmentOrders(Instant forTick) {
        return probeRepository
                .findAll()
                .flatMap(probe -> Mono.justOrEmpty(probe
                                .getOrdersToEstablish()
                                .stream()
                                .filter(canExecuteOrder(forTick))
                                .findFirst()
                        )
                        .flatMap(establishSelfAsHarvester -> orderExecutingService.establishProbeAsHarvester(forTick, establishSelfAsHarvester, probe)));
    }
}
