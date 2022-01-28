package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@RelationshipEntity(type = "LINKED_TO")
class StarlineLink extends Entity {
    @StartNode
    StarEntity linkFrom;
    @EndNode
    StarEntity linkTo;

    UUID starlineID;

    Integer sequesteredMass;
}
