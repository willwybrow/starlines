package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.types.spatial.CartesianPoint3d;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NodeEntity(label = "Star")
@CompositeIndex(properties = {"clusterID", "coordinate"})
public class StarEntity extends Entity {
    Long clusterID;
    CartesianPoint3d coordinate;
    String name;
    Integer currentMass;
    Integer maximumMass;

    @Relationship(type = "LINKED_TO")
    Set<StarEntity> linkedTo;

    public static StarEntity from(Star star, ClusterID inCluster) {
        return StarEntity
                .builder()
                .clusterID(inCluster.getNumeric())
                .coordinate(new CartesianPoint3d((double)star.getCoordinate().q(), (double)star.getCoordinate().r(), (double)star.getCoordinate().s()))
                .currentMass(star.getCurrentMass())
                .maximumMass(star.getMaximumMass())
                .build();
    }

    public Star toStar() {
        return new Star(
                this.id,
                new HexPoint((int)this.coordinate.getX(),
                        (int)this.coordinate.getY()),
                this.name,
                this.currentMass,
                this.maximumMass
        );
    }
}

