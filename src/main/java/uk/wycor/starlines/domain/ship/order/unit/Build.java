package uk.wycor.starlines.domain.ship.order.unit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.ship.order.RepeatableOrder;

@Node("Build")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public abstract class Build extends RepeatableOrder {
}
