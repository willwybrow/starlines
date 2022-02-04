package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import uk.wycor.starlines.domain.Harvester;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NodeEntity(label = "Harvester")
public class HarvesterEntity extends ShipEntity {

    public Harvester toShip() {
        return new Harvester(this.id, this.ownedBy.toPlayer());
    }
}
