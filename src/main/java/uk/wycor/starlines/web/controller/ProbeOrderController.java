package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.ProbeOrders;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.web.filter.AuthenticationFilter;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProbeOrderController {
    private final ProbeOrders probeOrders;
    private final ProbeRepository probeRepository;

    @Autowired
    public ProbeOrderController(ProbeOrders probeOrders, ProbeRepository probeRepository) {
        this.probeOrders = probeOrders;
        this.probeRepository = probeRepository;
    }


    @PostMapping(
            path = "/api/ships/my/probes/{probeID}/orders/establish-as-harvester",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Probe> orderProbeToEstablishAsHarvester(@PathVariable("probeID") UUID probeID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        probeRepository
                                .findById(probeID)
                                .flatMap(probe -> probeOrders.orderProbeToEstablishSelf(player, probe))
                );
    }
}
