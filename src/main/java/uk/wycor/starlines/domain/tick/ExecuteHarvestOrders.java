package uk.wycor.starlines.domain.tick;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.order.harvest.Harvest;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StabiliserRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.time.Instant;
import java.util.logging.Logger;

@Component
@org.springframework.core.annotation.Order(1)
public class ExecuteHarvestOrders extends ExecuteOrders<Harvest> {
    private static final Logger logger = Logger.getLogger(ExecuteHarvestOrders.class.getName());

    private final HarvesterRepository harvesterRepository;
    private final StarRepository starRepository;

    public ExecuteHarvestOrders(OrderRepository orderRepository, HarvesterRepository harvesterRepository, ProbeRepository probeRepository, StabiliserRepository stabiliserRepository, StarRepository starRepository) {
        super(orderRepository);
        this.harvesterRepository = harvesterRepository;
        this.starRepository = starRepository;
    }

    @Neo4jTransactional
    @Override
    public Flux<Harvest> executeOrders(Instant forTick, Instant nextTick) {
        return harvesterRepository
                .findAll()
                .flatMap(harvester -> Mono
                        .justOrEmpty(harvester.getOrdersToHarvest().stream().filter(canExecuteOrder(forTick)).findFirst())
                        .flatMap(harvest -> harvest(forTick, harvest, harvester))
                );
    }

    private Mono<Harvest> harvest(Instant executionTick, Harvest order, Harvester harvester) {
        logger.info(String.format("Harvester %s has an order to harvest!", harvester.getId().toString()));
        Star orbiting = harvester.getOrbiting();
        orbiting.harvestMass();
        order.setExecutedAt(executionTick);
        return starRepository.save(orbiting)
                .then(orderRepository.save(order))
                .then(Mono.just(order));
    }
}
