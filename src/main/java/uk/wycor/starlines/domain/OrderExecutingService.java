package uk.wycor.starlines.domain;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StabiliserRepository;

import java.util.logging.Logger;

@Service
public class OrderExecutingService {
    private final static Logger logger = Logger.getLogger(OrderExecutingService.class.getName());

    private final OrderRepository orderRepository;
    private final ProbeRepository probeRepository;
    private final HarvesterRepository harvesterRepository;
    private final StabiliserRepository stabiliserRepository;

    public OrderExecutingService(OrderRepository orderRepository, ProbeRepository probeRepository, HarvesterRepository harvesterRepository, StabiliserRepository stabiliserRepository) {
        this.orderRepository = orderRepository;
        this.probeRepository = probeRepository;
        this.harvesterRepository = harvesterRepository;
        this.stabiliserRepository = stabiliserRepository;
    }

    @Neo4jTransactional
    public Mono<Harvester> establishProbeAsHarvester(Order order, Probe probe) {
        logger.info(String.format("Probe %s has an outstanding order to establish itself as a Harvester", probe.getId().toString()));
        Star orbiting = probe.getOrbiting();
        Player owner = probe.getOwner();
        orbiting.getProbesInOrbit().remove(probe);
        Harvester harvester = new Harvester(probe.getId(), owner, orbiting);
        orbiting.getProbesInOrbit().remove(probe);
        return harvesterRepository.save(harvester)
                .then(orderRepository.delete(order))
                .then(probeRepository.deleteById(probe.getId()))
                .then(Mono.just(harvester));
    }
}
