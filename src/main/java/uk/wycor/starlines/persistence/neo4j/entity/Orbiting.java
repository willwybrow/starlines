package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@RelationshipEntity(type = "ORBITING")
public class Orbiting extends Relationship {

    @StartNode
    ProbeEntity orbiter;

    @EndNode
    StarEntity orbiting;
}

