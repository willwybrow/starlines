package uk.wycor.starlines.domain.ship.order;

import org.springframework.data.neo4j.core.schema.Node;

@Node("Stabilise")
public class Stabilise extends RepeatableOrder {
}