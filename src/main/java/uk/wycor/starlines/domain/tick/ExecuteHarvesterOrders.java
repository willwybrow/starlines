package uk.wycor.starlines.domain.tick;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.player.Player;
import uk.wycor.starlines.domain.order.BuildHarvester;
import uk.wycor.starlines.domain.order.BuildProbe;
import uk.wycor.starlines.domain.order.BuildStabiliser;
import uk.wycor.starlines.domain.order.Harvest;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.ship.Stabiliser;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StabiliserRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Component
@org.springframework.core.annotation.Order(1)
public class ExecuteHarvesterOrders extends ExecuteOrders {
    private static final Logger logger = Logger.getLogger(ExecuteHarvesterOrders.class.getName());

    private final HarvesterRepository harvesterRepository;
    private final ProbeRepository probeRepository;
    private final StabiliserRepository stabiliserRepository;
    private final StarRepository starRepository;

    public ExecuteHarvesterOrders(OrderRepository orderRepository, HarvesterRepository harvesterRepository, ProbeRepository probeRepository, StabiliserRepository stabiliserRepository, StarRepository starRepository) {
        super(orderRepository);
        this.harvesterRepository = harvesterRepository;
        this.probeRepository = probeRepository;
        this.stabiliserRepository = stabiliserRepository;
        this.starRepository = starRepository;
    }

    @Override
    public Flux<Order> executeOrders(Instant thisTick, Instant nextTick) {
        return executeHarvestOrders(thisTick).map(harvest -> (Order)harvest)
                .concatWith(executeBuildProbeOrders(thisTick).map(order -> (Order)order))
                .concatWith(executeBuildHarvesterOrders(thisTick).map(order -> (Order)order))
                .concatWith(executeBuildStabiliserOrders(thisTick).map(order -> (Order)order));
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
                        .flatMap(harvest -> harvest(forTick, harvest, harvester))
                );
    }

    @Neo4jTransactional
    private Flux<BuildProbe> executeBuildProbeOrders(Instant forTick) {
        return harvesterRepository
                .findAll()
                .flatMap(harvester -> Mono
                        .justOrEmpty(harvester.getOrdersToBuildProbe().stream().filter(canExecuteOrder(forTick)).findFirst())
                        .flatMap(buildProbe -> buildProbe(forTick, buildProbe, harvester))
                );
    }

    @Neo4jTransactional
    private Flux<BuildHarvester> executeBuildHarvesterOrders(Instant forTick) {
        return harvesterRepository
                .findAll()
                .flatMap(harvester -> Mono
                        .justOrEmpty(harvester.getOrdersToBuildHarvester().stream().filter(canExecuteOrder(forTick)).findFirst())
                        .flatMap(buildHarvester -> buildHarvester(forTick, buildHarvester, harvester))
                );
    }

    @Neo4jTransactional
    private Flux<BuildStabiliser> executeBuildStabiliserOrders(Instant forTick) {
        return harvesterRepository
                .findAll()
                .flatMap(harvester -> Mono
                        .justOrEmpty(harvester.getOrdersToBuildStabiliser().stream().filter(canExecuteOrder(forTick)).findFirst())
                        .flatMap(buildStabiliser -> buildStabiliser(forTick, buildStabiliser, harvester))
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

    private Mono<BuildProbe> buildProbe(Instant executionTick, BuildProbe order, Harvester harvester) {
        logger.info(String.format("Harvester %s has an order to build a probe!", harvester.getId().toString()));
        Star orbiting = harvester.getOrbiting();
        Player owner = harvester.getOwner();
        Probe newProbe = new Probe(UUID.randomUUID(), owner, orbiting);
        order.setExecutedAt(executionTick);
        return probeRepository.save(newProbe)
                .then(starRepository.save(orbiting))
                .then(orderRepository.save(order))
                .then(Mono.just(order));
    }

    private Mono<BuildHarvester> buildHarvester(Instant executionTick, BuildHarvester order, Harvester harvester) {
        logger.info(String.format("Harvester %s has an order to build a probe!", harvester.getId().toString()));
        Star orbiting = harvester.getOrbiting();
        Player owner = harvester.getOwner();
        Harvester newHarvester = new Harvester(UUID.randomUUID(), owner, orbiting);
        order.setExecutedAt(executionTick);
        return harvesterRepository.save(harvester)
                .then(starRepository.save(orbiting))
                .then(orderRepository.save(order))
                .then(Mono.just(order));
    }

    private Mono<BuildStabiliser> buildStabiliser(Instant executionTick, BuildStabiliser order, Harvester harvester) {
        logger.info(String.format("Harvester %s has an order to build a probe!", harvester.getId().toString()));
        Star orbiting = harvester.getOrbiting();
        Player owner = harvester.getOwner();
        Stabiliser newStabiliser = new Stabiliser(UUID.randomUUID(), owner, orbiting);
        order.setExecutedAt(executionTick);
        return stabiliserRepository.save(newStabiliser)
                .then(starRepository.save(orbiting))
                .then(orderRepository.save(order))
                .then(Mono.just(order));
    }
}
