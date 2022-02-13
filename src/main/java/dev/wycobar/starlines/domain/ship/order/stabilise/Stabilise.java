package dev.wycobar.starlines.domain.ship.order.stabilise;

import org.springframework.data.neo4j.core.schema.Node;
import dev.wycobar.starlines.domain.ship.order.RepeatableOrder;

@Node("Stabilise")
public class Stabilise extends RepeatableOrder {
}
