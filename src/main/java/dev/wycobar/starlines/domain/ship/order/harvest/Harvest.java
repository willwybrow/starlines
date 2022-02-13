package dev.wycobar.starlines.domain.ship.order.harvest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import dev.wycobar.starlines.domain.ship.order.RepeatableOrder;

@Node("Harvest")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Harvest extends RepeatableOrder {
}
