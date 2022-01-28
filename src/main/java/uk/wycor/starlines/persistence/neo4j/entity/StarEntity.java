package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.types.spatial.CartesianPoint2d;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NodeEntity(label = "Star") // TODO rename entities back or give them all proper labels
public class StarEntity extends Entity {
    Integer clusterID;
    CartesianPoint2d coordinate;
    String name;
    Integer currentMass;
    Integer maximumMass;

    @Relationship(type = "LINKED_TO")
    Set<StarEntity> linkedTo;
}

