package dev.wycobar.starlines.domain.ship.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@SuperBuilder
@Getter
@Setter
@Node("OneTimeOrder")
public abstract class OneTimeOrder extends Order {
    public OneTimeOrder() {
        this.executedAt = null;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }
}
