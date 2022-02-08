package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.OrderGivingService;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.web.filter.AuthenticationFilter;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProbeOrderController {
    private final OrderGivingService orderGivingService;
    private final ProbeRepository probeRepository;

    @Autowired
    public ProbeOrderController(OrderGivingService orderGivingService, ProbeRepository probeRepository) {
        this.orderGivingService = orderGivingService;
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
                                .flatMap(probe -> orderGivingService.orderProbeToEstablishSelf(player, probe))
                );
    }
}
