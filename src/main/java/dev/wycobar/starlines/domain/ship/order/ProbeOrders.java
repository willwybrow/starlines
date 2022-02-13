package dev.wycobar.starlines.domain.ship.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.domain.ship.order.starline.OpenStarline;
import dev.wycobar.starlines.domain.star.Star;
import dev.wycobar.starlines.domain.tick.TickService;
import dev.wycobar.starlines.persistence.neo4j.Neo4jTransactional;
import dev.wycobar.starlines.persistence.neo4j.OrderRepository;
import dev.wycobar.starlines.persistence.neo4j.ProbeRepository;

import java.util.Set;
import java.util.UUID;

@Service
public class ProbeOrders {
    private final TickService tickService;
    private final OrderRepository orderRepository;
    private final ProbeRepository probeRepository;

    @Autowired
    public ProbeOrders(TickService tickService, OrderRepository orderRepository, ProbeRepository probeRepository) {
        this.tickService = tickService;
        this.orderRepository = orderRepository;
        this.probeRepository = probeRepository;
    }

    @Neo4jTransactional
    public Mono<Probe> orderProbeToEstablishSelf(Player player, Probe proposedProbe) {
        return verifyProbeOwnership(player, proposedProbe)
                .flatMap(allowedProbe -> {
                    EstablishSelfAsHarvester order = EstablishSelfAsHarvester
                            .builder()
                            .id(UUID.randomUUID())
                            .scheduledFor(tickService.nextTick())
                            .executedAt(null)
                            .orderGivenTo(allowedProbe)
                            .build();
                    allowedProbe.setOrdersToEstablish(Set.of(order));
                    return orderRepository
                            .save(order)
                            .flatMap(o -> probeRepository.save(allowedProbe));
                });
    }

    @Neo4jTransactional
    public Mono<Probe> orderProbeToOpenStarline(Player player, Probe proposedProbe, Star destinationStar) {
        return verifyProbeOwnership(player, proposedProbe)
                .flatMap(allowedProbe -> {
                    OpenStarline order = OpenStarline
                            .builder()
                            .id(UUID.randomUUID())
                            .scheduledFor(tickService.nextTick())
                            .executedAt(null)
                            .orderGivenTo(allowedProbe)
                            .target(destinationStar)
                            .build();
                    allowedProbe.setOrdersToOpenStarline(Set.of(order));
                    return orderRepository
                            .save(order)
                            .then(probeRepository.save(allowedProbe));
                });
    }

    private Mono<Probe> verifyProbeOwnership(Player player, Probe probe) {
        return Mono.justOrEmpty(player.getOwnedProbes()
                .stream()
                .filter(probe::equals)
                .findFirst() // there should be at most one
        );
    }
}
