package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.types.spatial.CartesianPoint2d;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NodeEntity(label = "Star")
@CompositeIndex(properties = {"clusterID", "coordinate"})
public class StarEntity extends Entity {
    Integer clusterID;
    CartesianPoint2d coordinate;
    String name;
    Integer currentMass;
    Integer maximumMass;

    @Relationship(type = "LINKED_TO")
    Set<StarEntity> linkedTo;
}

