package uk.wycor.starlines.domain;

import java.util.UUID;

public class Harvester extends Ship {
    public Harvester(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
    }
}
