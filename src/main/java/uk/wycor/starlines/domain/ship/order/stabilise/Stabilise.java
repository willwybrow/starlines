package uk.wycor.starlines.domain.ship.order.stabilise;

import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.ship.order.RepeatableOrder;

@Node("Stabilise")
public class Stabilise extends RepeatableOrder {
}
