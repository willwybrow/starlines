package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RelationshipEntity(type = "LINKED_TO")
public class StarlineLink extends Relationship {
    @Index
    UUID starlineID;

    @StartNode
    StarEntity linkFrom;
    @EndNode
    StarEntity linkTo;

    Integer sequesteredMass;
}
