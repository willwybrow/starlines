package uk.wycor.starlines.persistence.neo4j.entity;

import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

public abstract class ShipEntity extends Entity {

    @Relationship(type = "ORBITING")
    Set<StarEntity> orbiting;
}
