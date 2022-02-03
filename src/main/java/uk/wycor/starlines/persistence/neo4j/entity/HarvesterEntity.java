package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Harvester;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NodeEntity(label = "Harvester")
@SuperBuilder
public class HarvesterEntity extends Entity {

    @Relationship(type = "ORBITING")
    StarEntity orbiting;

    @Relationship(type = "OWNED_BY")
    PlayerEntity ownedBy;

    public Harvester toShip() {
        return new Harvester(this.id, this.ownedBy.toPlayer());
    }
}
