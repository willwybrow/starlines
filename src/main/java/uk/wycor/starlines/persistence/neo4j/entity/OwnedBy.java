package uk.wycor.starlines.persistence.neo4j.entity;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "OWNED_BY")
public class OwnedBy {
    @StartNode
    ShipEntity ownedShip;
    @EndNode
    PlayerEntity ownedByPlayer;

}
