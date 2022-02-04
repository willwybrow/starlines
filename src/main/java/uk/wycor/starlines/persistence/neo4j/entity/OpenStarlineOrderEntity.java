package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.UUID;

@NodeEntity(label = "OpenStarlineOrder")
@SuperBuilder
public class OpenStarlineOrderEntity extends OrderEntity {
    UUID targetStarID;
}
