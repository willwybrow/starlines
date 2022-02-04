package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Stabiliser;

@Getter
@Setter

@NoArgsConstructor
@NodeEntity(label = "Stabiliser")
@SuperBuilder
public class StabiliserEntity extends Entity {

    @Relationship(type = "ORBITING")
    StarEntity orbiting;

    @Relationship(type = "OWNED_BY")
    PlayerEntity ownedBy;

    public Stabiliser toShip() {
        return new Stabiliser(this.id, this.ownedBy.toPlayer());
    }
}
