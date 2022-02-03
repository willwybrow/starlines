package uk.wycor.starlines.domain;

import java.util.UUID;

public abstract class Ship extends GameObject {
    public final Player ownedBy;
    public Ship(UUID id, Player ownedBy) {
        super(id);
        this.ownedBy = ownedBy;
    }
}
