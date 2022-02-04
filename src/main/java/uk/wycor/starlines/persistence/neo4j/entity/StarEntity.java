package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.types.spatial.CartesianPoint3d;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Probe;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;
import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;

@Getter
@Setter
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
    Integer naturalMassCapacity;
    Integer stabilisation;
    Integer accumulatedInstability;

    @Relationship(type = "LINKED_TO", direction = UNDIRECTED)
    Set<StarEntity> linkedTo;

    @Relationship(type = "ORBITING", direction = INCOMING)
    Set<ProbeEntity> orbitedByProbes;

    public static StarEntity from(Star star) {
        // new StarEntity(nextClusterID.getNumeric(), new CartesianPoint3d(hexPoint.q(), hexPoint.r(), hexPoint.s()), star.getName(), star.getCurrentMass(), star.getNaturalMassCapacity(), Collections.emptySet()))
        return StarEntity
                .builder()
                .clusterID(star.getLocation().getNumeric())
                .coordinate(new CartesianPoint3d((double)star.getCoordinate().q(), (double)star.getCoordinate().r(), (double)star.getCoordinate().s()))
                .name(star.getName())
                .currentMass(star.getCurrentMass())
                .naturalMassCapacity(star.getNaturalMassCapacity())
                .stabilisation(star.getStabilisation())
                .accumulatedInstability(star.getAccumulatedInstability())
                .build();
    }

    public Star toStar() {
        return new Star(
                this.id,
                new ClusterID(this.clusterID),
                new HexPoint((long)this.coordinate.getX(),
                        (long)this.coordinate.getY()),
                this.name,
                this.currentMass,
                this.naturalMassCapacity,
                this.stabilisation,
                this.accumulatedInstability
        );
    }

    public Set<Probe> shipsInOrbit() {
        return Optional
                .ofNullable(this.orbitedByProbes)
                .orElseGet(Collections::emptySet)
                .stream()
                .map(ProbeEntity::toShip)
                .collect(Collectors.toSet());
    }
}

