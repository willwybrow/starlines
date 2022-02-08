package uk.wycor.starlines.domain.ship;

import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.star.Star;

import java.util.UUID;

public class Stabiliser extends Ship {
    public Stabiliser(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
    }
}
