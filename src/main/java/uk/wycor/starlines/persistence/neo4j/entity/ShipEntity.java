package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Ship;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "Ship")
@SuperBuilder
public abstract class ShipEntity extends Entity {

    @Relationship(type = "ORBITING")
    StarEntity orbiting;

    @Relationship(type = "OWNED_BY")
    PlayerEntity ownedBy;

    public abstract Ship toShip();
}
