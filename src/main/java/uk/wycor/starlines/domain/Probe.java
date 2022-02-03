package uk.wycor.starlines.domain;

import java.util.UUID;

public class Probe extends Ship {
    public Probe(UUID id, Player ownedBy) {
        super(id, ownedBy);
    }
}
