package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Probe;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "Probe")
@SuperBuilder
public class ProbeEntity extends Entity {
    @Relationship(type = "ORBITING")
    StarEntity orbiting;

    @Relationship(type = "OWNED_BY")
    PlayerEntity ownedBy;

    public Probe toShip() {
        return new Probe(this.id, this.ownedBy.toPlayer());
    }
}
