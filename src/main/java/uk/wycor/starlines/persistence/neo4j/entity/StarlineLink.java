package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@RelationshipEntity(type = "LINKED_TO")
public class StarlineLink extends Relationship {
    @Index
    @Convert(UuidStringConverter.class)
    UUID starlineID;

    @StartNode
    StarEntity linkFrom;
    @EndNode
    StarEntity linkTo;

    Long sequesteredMass;

}
