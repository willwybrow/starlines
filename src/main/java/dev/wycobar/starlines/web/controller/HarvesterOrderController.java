package dev.wycobar.starlines.web.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.ship.order.HarvesterOrders;
import dev.wycobar.starlines.domain.ship.Harvester;
import dev.wycobar.starlines.persistence.neo4j.HarvesterRepository;
import dev.wycobar.starlines.web.filter.AuthenticationFilter;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HarvesterOrderController {
    private final HarvesterOrders harvesterOrders;
    private final HarvesterRepository harvesterRepository;

    public HarvesterOrderController(HarvesterOrders harvesterOrders, HarvesterRepository harvesterRepository) {
        this.harvesterOrders = harvesterOrders;
        this.harvesterRepository = harvesterRepository;
    }

    @PostMapping(
            path = "/api/ships/my/harvesters/{harvesterID}/orders/harvest",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Harvester> orderHarvesterToHarvest(@PathVariable("harvesterID") UUID harvesterID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        harvesterRepository
                                .findById(harvesterID)
                                .flatMap(harvester -> harvesterOrders.harvest(player, harvester))
                );
    }

    @PostMapping(
            path = "/api/ships/my/harvesters/{harvesterID}/orders/build-probe",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Harvester> orderHarvesterToBuildProbe(@PathVariable("harvesterID") UUID harvesterID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        harvesterRepository
                                .findById(harvesterID)
                                .flatMap(harvester -> harvesterOrders.buildProbe(player, harvester))
                );
    }

    @PostMapping(
            path = "/api/ships/my/harvesters/{harvesterID}/orders/build-harvester",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Harvester> orderHarvesterToBuildHarvester(@PathVariable("harvesterID") UUID harvesterID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        harvesterRepository
                                .findById(harvesterID)
                                .flatMap(harvester -> harvesterOrders.buildHarvester(player, harvester))
                );
    }

    @PostMapping(
            path = "/api/ships/my/harvesters/{harvesterID}/orders/build-stabiliser",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<Harvester> orderHarvesterToBuildStabiliser(@PathVariable("stabiliserID") UUID harvesterID) {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .flatMap(player ->
                        harvesterRepository
                                .findById(harvesterID)
                                .flatMap(harvester -> harvesterOrders.buildStabiliser(player, harvester))
                );
    }
}
