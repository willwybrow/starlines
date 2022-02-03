package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@RelationshipEntity(type = "HAS_ORDER")
public class HasOrder extends Entity {
    @StartNode
    ShipEntity ship;
    @EndNode
    OrderEntity order;
}
