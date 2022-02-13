package dev.wycobar.starlines.domain.tick;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.Harvester;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.domain.ship.Stabiliser;
import dev.wycobar.starlines.domain.ship.order.unit.Build;
import dev.wycobar.starlines.domain.ship.order.unit.BuildHarvester;
import dev.wycobar.starlines.domain.ship.order.unit.BuildProbe;
import dev.wycobar.starlines.domain.ship.order.unit.BuildStabiliser;
import dev.wycobar.starlines.domain.star.Star;
import dev.wycobar.starlines.persistence.neo4j.HarvesterRepository;
import dev.wycobar.starlines.persistence.neo4j.Neo4jTransactional;
import dev.wycobar.starlines.persistence.neo4j.OrderRepository;
import dev.wycobar.starlines.persistence.neo4j.ProbeRepository;
import dev.wycobar.starlines.persistence.neo4j.StabiliserRepository;
import dev.wycobar.starlines.persistence.neo4j.StarRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@org.springframework.core.annotation.Order(1)
public class ExecuteBuildOrders extends ExecuteOrders<Build> {
    private static final Logger logger = Logger.getLogger(ExecuteBuildOrders.class.getName());

    private final HarvesterRepository harvesterRepository;
    private final ProbeRepository probeRepository;
    private final StabiliserRepository stabiliserRepository;
    private final StarRepository starRepository;

    public ExecuteBuildOrders(OrderRepository orderRepository, HarvesterRepository harvesterRepository, ProbeRepository probeRepository, StabiliserRepository stabiliserRepository, StarRepository starRepository) {
        super(orderRepository);
        this.harvesterRepository = harvesterRepository;
        this.probeRepository = probeRepository;
        this.stabiliserRepository = stabiliserRepository;
        this.starRepository = starRepository;
    }

    @Override
    public Flux<Build> executeOrders(Instant thisTick, Instant nextTick) {
        return executeBuildProbeOrders(thisTick).map(order -> (Build)order)
                .concatWith(executeBuildHarvesterOrders(thisTick).map(order -> (Build)order))
                .concatWith(executeBuildStabiliserOrders(thisTick).map(order -> (Build)order));
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
        return harvesterRepository.save(newHarvester)
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
