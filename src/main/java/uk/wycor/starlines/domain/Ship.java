package uk.wycor.starlines.domain;

import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Ship")
public abstract class Ship extends GameObject {
    public final Player ownedBy;
    public Ship(UUID id, Player ownedBy) {
        super(id);
        this.ownedBy = ownedBy;
    }
}
