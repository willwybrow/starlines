package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import uk.wycor.starlines.domain.Stabiliser;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NodeEntity(label = "Stabiliser")
public class StabiliserEntity extends ShipEntity {

    public Stabiliser toShip() {
        return new Stabiliser(this.id, this.ownedBy.toPlayer());
    }
}
