package dev.wycobar.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.ship.order.ProbeOrders;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.persistence.neo4j.ProbeRepository;
import dev.wycobar.starlines.persistence.neo4j.StarRepository;
import dev.wycobar.starlines.web.filter.AuthenticationFilter;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProbeOrderController {
    private final ProbeOrders probeOrders;
    private final ProbeRepository probeRepository;
    private final StarRepository starRepository;

    @Autowired
    public ProbeOrderController(ProbeOrders probeOrders, ProbeRepository probeRepository, StarRepository starRepository) {
        this.probeOrders = probeOrders;
        this.probeRepository = probeRepository;
        this.starRepository = starRepository;
    }

    @PostMapping(
            path = "/api/ships/my/probes/{probeID}/orders/deploy-harvester",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Probe> orderProbeToDeployHarvester(@PathVariable("probeID") UUID probeID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        probeRepository
                                .findById(probeID)
                                .flatMap(probe -> probeOrders.orderProbeToDeploy(player, probe))
                );
    }

    @PostMapping(
            path = "/api/ships/my/probes/{probeID}/orders/open-starline/{starID}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Probe> orderProbeToOpenStarline(@PathVariable("probeID") UUID probeID, @PathVariable("starID") UUID starID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        probeRepository
                                .findById(probeID)
                                .flatMap(probe -> starRepository.findById(starID).flatMap(star -> probeOrders.orderProbeToOpenStarline(player, probe, star)))
                                .flatMap(probe -> probeRepository.findById(probe.getId()))
                );
    }
}
