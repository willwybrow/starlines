package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.web.filter.AuthenticationFilter;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HarvestersController {

    @Autowired
    public HarvestersController() {
    }

    @GetMapping(
            path = "/api/ships/my/harvesters/", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Set<Harvester>> getMyHarvesters() {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .map(Player::getOwnedHarvesters);
    }
}
