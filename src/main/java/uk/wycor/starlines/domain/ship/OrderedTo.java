package uk.wycor.starlines.domain.ship;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import uk.wycor.starlines.domain.GameObjectRelationship;
import uk.wycor.starlines.domain.order.Order;

import java.util.Set;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
public class OrderedTo extends GameObjectRelationship {
    @TargetNode
    Set<Order> orders;
}
