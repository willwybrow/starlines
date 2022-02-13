package uk.wycor.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.player.Player;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.ship.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;

import java.time.Instant;
import java.util.logging.Logger;

@Component
@org.springframework.core.annotation.Order(5)
public class ExecuteProbeEstablishmentOrders extends ExecuteOrders<EstablishSelfAsHarvester> {
    private static final Logger logger = Logger.getLogger(ExecuteProbeEstablishmentOrders.class.getName());

    private final ProbeRepository probeRepository;
    private final HarvesterRepository harvesterRepository;

    @Autowired
    public ExecuteProbeEstablishmentOrders(OrderRepository orderRepository,
                                           ProbeRepository probeRepository,
                                           HarvesterRepository harvesterRepository) {
        super(orderRepository);
        this.probeRepository = probeRepository;
        this.harvesterRepository = harvesterRepository;
    }

    @Override
    public Flux<EstablishSelfAsHarvester> executeOrders(Instant thisTick, Instant nextTick) {
        return executeProbeEstablishmentOrders(thisTick);
    }

    private Flux<EstablishSelfAsHarvester> executeProbeEstablishmentOrders(Instant forTick) {
        return probeRepository
                .findAll()
                .flatMap(probe -> Mono.justOrEmpty(probe
                                .getOrdersToEstablish()
                                .stream()
                                .filter(canExecuteOrder(forTick))
                                .findFirst()
                        )
                        .flatMap(establishSelfAsHarvester -> establishProbeAsHarvester(forTick, establishSelfAsHarvester, probe)));
    }

    @Neo4jTransactional
    private Mono<EstablishSelfAsHarvester> establishProbeAsHarvester(Instant executionTick, EstablishSelfAsHarvester order, Probe probe) {
        logger.info(String.format("Probe %s has an outstanding order to establish itself as a Harvester", probe.getId().toString()));
        Star orbiting = probe.getOrbiting();
        Player owner = probe.getOwner();
        Harvester harvester = new Harvester(probe.getId(), owner, orbiting);
        orbiting.getProbesInOrbit().remove(probe);
        order.setExecutedAt(executionTick);
        return harvesterRepository.save(harvester)
                .then(orderRepository.save(order))
                .then(probeRepository.deleteById(probe.getId()))
                .then(Mono.just(order));
    }
}
