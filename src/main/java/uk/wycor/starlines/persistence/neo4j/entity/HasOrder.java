package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;


@Getter
@Setter
@NoArgsConstructor
@RelationshipEntity(type = "HAS_ORDER")
public class HasOrder extends Entity {
    @StartNode
    ShipEntity ship;
    @EndNode
    OrderEntity order;
}
