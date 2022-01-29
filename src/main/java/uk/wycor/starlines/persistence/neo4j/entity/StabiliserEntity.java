package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Stabiliser")
@SuperBuilder
public class StabiliserEntity extends ShipEntity {
}
