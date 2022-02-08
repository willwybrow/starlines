package uk.wycor.starlines.domain.order;

import org.springframework.data.neo4j.core.schema.Node;

@Node("Harvest")
public class Harvest extends RepeatableOrder {
}
