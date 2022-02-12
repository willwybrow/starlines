package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.order.Harvest;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.tick.TickService;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.util.Collections;
import java.util.UUID;

@Service
public class OrderGivingService {

    private final TickService tickService;
    private final OrderRepository orderRepository;
    private final ProbeRepository probeRepository;
    private final HarvesterRepository harvesterRepository;
    private final StarRepository starRepository;

    @Autowired
    public OrderGivingService(TickService tickService, OrderRepository orderRepository, ProbeRepository probeRepository, HarvesterRepository harvesterRepository, StarRepository starRepository) {
        this.tickService = tickService;
        this.orderRepository = orderRepository;
        this.probeRepository = probeRepository;
        this.harvesterRepository = harvesterRepository;
        this.starRepository = starRepository;
    }

    @Neo4jTransactional
    public Mono<Probe> orderProbeToEstablishSelf(Player player, Probe proposedProbe) {
        return Mono.justOrEmpty(player.getOwnedProbes()
                        .stream()
                        .filter(proposedProbe::equals)
                        .findFirst() // there should be at most one
                )
                .flatMap(allowedProbe -> {
                    EstablishSelfAsHarvester order = EstablishSelfAsHarvester
                            .builder()
                            .id(UUID.randomUUID())
                            .scheduledFor(tickService.nextTick())
                            .executedAt(null)
                            .orderGivenTo(allowedProbe)
                            .build();
                    allowedProbe.getOrdersToEstablish().add(order);
                    allowedProbe.getOrdersToEstablish().retainAll(Collections.singleton(order));
                    return orderRepository
                            .save(order)
                            .flatMap(o -> probeRepository.save(allowedProbe));
                });
    }

    public Mono<Harvester> orderHarvesterToHarvest(Player player, Harvester proposedHarvester) {
        return Mono.justOrEmpty(player.getOwnedHarvesters()
                        .stream()
                        .filter(proposedHarvester::equals)
                        .findFirst() // there should be at most one
                )
                .flatMap(allowedHarvester -> {
                    Harvest order = Harvest
                            .builder()
                            .id(UUID.randomUUID())
                            .scheduledFor(tickService.nextTick())
                            .executedAt(null)
                            .orderGivenTo(allowedHarvester)
                            .build();
                    allowedHarvester.getOrdersToHarvest().add(order);
                    allowedHarvester.getOrdersToHarvest().retainAll(Collections.singleton(order));
                    return orderRepository
                            .save(order)
                            .flatMap(o -> harvesterRepository.save(allowedHarvester));
                });

    }
}