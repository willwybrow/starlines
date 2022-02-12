package uk.wycor.starlines.domain.ship.order;

import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@SuperBuilder
@Node("CloseStarline")
public class CloseStarline extends OneTimeOrder {
    /* Starline target; */
}
