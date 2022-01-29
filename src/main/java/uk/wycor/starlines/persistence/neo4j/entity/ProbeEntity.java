package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Probe")
@SuperBuilder
public class ProbeEntity extends ShipEntity {
}
