package uk.wycor.starlines.domain.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.GameObject;

import java.time.LocalDateTime;

@Node("Order")
@Getter
@Setter
public abstract class Order extends GameObject {
    private LocalDateTime executedAt;

    public Order() {
        this.executedAt = null;
    }

    public abstract boolean isRepeatable();
}
