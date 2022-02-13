package uk.wycor.starlines.domain.ship.order.starline;

import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.ship.order.OneTimeOrder;

@SuperBuilder
@Node("CloseStarline")
public class CloseStarline extends OneTimeOrder {
    /* Starline target; */
}
