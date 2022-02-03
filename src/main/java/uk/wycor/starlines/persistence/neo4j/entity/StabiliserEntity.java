package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Stabiliser;

@Data
@EqualsAndHashCode(callSuper = true)
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
