package dev.wycobar.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.Harvester;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.domain.ship.order.DeployHarvesterOrder;
import dev.wycobar.starlines.domain.star.Star;
import dev.wycobar.starlines.persistence.neo4j.HarvesterRepository;
import dev.wycobar.starlines.persistence.neo4j.Neo4jTransactional;
import dev.wycobar.starlines.persistence.neo4j.OrderRepository;
import dev.wycobar.starlines.persistence.neo4j.ProbeRepository;

import java.time.Instant;
import java.util.logging.Logger;

@Component
@org.springframework.core.annotation.Order(5)
public class ExecuteDeployHarvesterOrders extends ExecuteOrders<DeployHarvesterOrder> {
    private static final Logger logger = Logger.getLogger(ExecuteDeployHarvesterOrders.class.getName());

    private final ProbeRepository probeRepository;
    private final HarvesterRepository harvesterRepository;

    @Autowired
    public ExecuteDeployHarvesterOrders(OrderRepository orderRepository,
                                        ProbeRepository probeRepository,
                                        HarvesterRepository harvesterRepository) {
        super(orderRepository);
        this.probeRepository = probeRepository;
        this.harvesterRepository = harvesterRepository;
    }

    @Override
    public Flux<DeployHarvesterOrder> executeOrders(Instant thisTick) {
        return probeRepository
                .findAll()
                .flatMap(probe -> Mono.justOrEmpty(probe
                                .getOrdersToDeploy()
                                .stream()
                                .filter(canExecuteOrder(thisTick))
                                .findFirst()
                        )
                        .flatMap(deployHarvesterOrder -> deployHarvester(thisTick, deployHarvesterOrder, probe)));
    }

    @Neo4jTransactional
    private Mono<DeployHarvesterOrder> deployHarvester(Instant executionTick, DeployHarvesterOrder order, Probe probe) {
        logger.info(String.format("Probe %s has an outstanding order to deploy itself as a Harvester", probe.getId().toString()));
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
