package uk.wycor.starlines.persistence.neo4j.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.UuidStrategy;

import java.util.UUID;

abstract class Entity {
    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    UUID id;

}
