package uk.wycor.starlines.domain.tick;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.OrderExecutingService;
import uk.wycor.starlines.domain.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.order.Harvest;
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
public class ExecuteOrders implements OnTickAction {
    private final OrderRepository orderRepository;
    private final ProbeRepository probeRepository;
    private final HarvesterRepository harvesterRepository;
    private final StabiliserRepository stabiliserRepository;
    private final OrderExecutingService orderExecutingService;

    public ExecuteOrders(OrderRepository orderRepository, ProbeRepository probeRepository, HarvesterRepository harvesterRepository, StabiliserRepository stabiliserRepository, OrderExecutingService orderExecutingService) {
        this.orderRepository = orderRepository;
        this.probeRepository = probeRepository;
        this.harvesterRepository = harvesterRepository;
        this.stabiliserRepository = stabiliserRepository;
        this.orderExecutingService = orderExecutingService;
    }

    @Override
    public Flux<Void> processActions(Instant thisTick, Instant nextTick) {
        return executeAllOrdersAndRefreshRepeatable(thisTick, nextTick);
    }

    private Flux<Void> executeAllOrdersAndRefreshRepeatable(Instant thisTick, Instant nextTick) {
        return executeProbeEstablishmentOrders(thisTick)
                .map(establishSelfAsHarvester -> (Order)establishSelfAsHarvester)
                .mergeWith(executeHarvestOrders(thisTick))
                .flatMap(order -> {
                    if (order.isRepeatable()) {
                        order.setScheduledFor(nextTick);
                        return orderRepository.save(order).then();
                    }
                    return Mono.empty();
                });
    }

    private Predicate<uk.wycor.starlines.domain.order.Order> canExecuteOrder(Instant onThisTick) {
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
