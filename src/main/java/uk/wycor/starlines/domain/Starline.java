package uk.wycor.starlines.domain;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;


@Getter
public class Starline extends GameObject {
    private final Set<StarlineLeg> network;

    public Starline(UUID id, Set<StarlineLeg> network) {
        super(id);
        this.network = network;
    }
}
