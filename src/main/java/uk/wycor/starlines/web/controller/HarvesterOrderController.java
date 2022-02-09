package uk.wycor.starlines.web.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.OrderGivingService;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.persistence.neo4j.HarvesterRepository;
import uk.wycor.starlines.web.filter.AuthenticationFilter;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HarvesterOrderController {
    private final OrderGivingService orderGivingService;
    private final HarvesterRepository harvesterRepository;

    public HarvesterOrderController(OrderGivingService orderGivingService, HarvesterRepository harvesterRepository) {
        this.orderGivingService = orderGivingService;
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
                                .flatMap(harvester -> orderGivingService.orderHarvesterToHarvest(player, harvester))
                );
    }
}
