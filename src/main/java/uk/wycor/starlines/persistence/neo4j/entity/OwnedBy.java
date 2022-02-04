package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RelationshipEntity(type = "OWNED_BY")
public class OwnedBy extends Relationship {
    @StartNode
    ProbeEntity ownedProbe;
    @EndNode
    PlayerEntity ownedByPlayer;

}
