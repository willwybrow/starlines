package uk.wycor.starlines.persistence.neo4j.entity;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "STARTED_AT")
public class StartedAt {
    @StartNode
    PlayerEntity player;
    @EndNode
    StarEntity startedAt;

}
