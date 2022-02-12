package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.ship.order.BuildHarvester;
import uk.wycor.starlines.domain.ship.order.BuildProbe;
import uk.wycor.starlines.domain.ship.order.BuildStabiliser;
import uk.wycor.starlines.domain.ship.order.Harvest;
import uk.wycor.starlines.domain.player.Player;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.tick.TickService;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;

import java.util.Set;
import java.util.UUID;

@Service
public class HarvesterOrders {

    private final TickService tickService;
    private final OrderRepository orderRepository;
    private final HarvesterRepository harvesterRepository;

    @Autowired
    public HarvesterOrders(TickService tickService, OrderRepository orderRepository, HarvesterRepository harvesterRepository) {
        this.tickService = tickService;
        this.orderRepository = orderRepository;
        this.harvesterRepository = harvesterRepository;
    }

    @Neo4jTransactional
    public Mono<Harvester> harvest(Player player, Harvester orderTarget) {
        return verifyHarvesterOwnership(player, orderTarget)
                .flatMap(allowedHarvester -> {
                    Harvest order = Harvest
                            .builder()
                            .id(UUID.randomUUID())
                            .scheduledFor(tickService.nextTick())
                            .executedAt(null)
                            .orderGivenTo(allowedHarvester)
                            .build();
                    allowedHarvester.setOrdersToHarvest(Set.of(order));
                    return orderRepository
                            .save(order)
                            .flatMap(o -> harvesterRepository.save(allowedHarvester));
                });
    }

    @Neo4jTransactional
    public Mono<Harvester> buildProbe(Player player, Harvester orderTarget) {
        return verifyHarvesterOwnership(player, orderTarget)
                .flatMap(allowedHarvester -> {
                    BuildProbe order = BuildProbe.builder().id(UUID.randomUUID()).build();
                    allowedHarvester.setOrdersToBuildProbe(
                            Set.of(order)
                    );
                    return orderRepository
                            .save(order)
                            .flatMap(o -> harvesterRepository.save(allowedHarvester));
                });
    }

    @Neo4jTransactional
    public Mono<Harvester> buildHarvester(Player player, Harvester orderTarget) {
        return verifyHarvesterOwnership(player, orderTarget)
                .flatMap(allowedHarvester -> {
                    BuildHarvester order = BuildHarvester.builder().id(UUID.randomUUID()).build();
                    allowedHarvester.setOrdersToBuildHarvester(
                            Set.of(order)
                    );
                    return orderRepository
                            .save(order)
                            .flatMap(o -> harvesterRepository.save(allowedHarvester));
                });
    }

    @Neo4jTransactional
    public Mono<Harvester> buildStabiliser(Player player, Harvester orderTarget) {
        return verifyHarvesterOwnership(player, orderTarget)
                .flatMap(allowedHarvester -> {
                    BuildStabiliser order = BuildStabiliser.builder().id(UUID.randomUUID()).build();
                    allowedHarvester.setOrdersToBuildStabiliser(
                            Set.of(order)
                    );
                    return orderRepository
                            .save(order)
                            .flatMap(o -> harvesterRepository.save(allowedHarvester));
                });
    }

    private Mono<Harvester> verifyHarvesterOwnership(Player player, Harvester harvester) {
        return Mono.justOrEmpty(player.getOwnedHarvesters()
                .stream()
                .filter(harvester::equals)
                .findFirst() // there should be at most one
        );
    }
}
