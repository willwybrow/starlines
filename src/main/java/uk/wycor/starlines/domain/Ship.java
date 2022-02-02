package uk.wycor.starlines.domain;

import java.util.UUID;

public abstract class Ship extends GameObject {
    public Ship(UUID id) {
        super(id);
    }
}
