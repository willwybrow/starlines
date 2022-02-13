package dev.wycobar.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.Probe;
import dev.wycobar.starlines.web.filter.AuthenticationFilter;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProbesController {

    @Autowired
    public ProbesController() {
    }

    @GetMapping(
            path = "/api/ships/my/probes/", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Set<Probe>> getMyProbes() {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .map(Player::getOwnedProbes);
    }
}
