package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Harvester")
@SuperBuilder
public class HarvesterEntity extends ShipEntity {
}
