package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Harvester;

@Getter
@Setter
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
