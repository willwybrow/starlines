package dev.wycobar.starlines.domain.ship.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import dev.wycobar.starlines.domain.GameObject;
import dev.wycobar.starlines.domain.ship.Ship;

import java.time.Instant;

@Node("Order")
@Getter
@Setter
@SuperBuilder
@JsonSerialize
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "orderType")
public abstract class Order extends GameObject {
    @JsonProperty
    Instant scheduledFor;

    @JsonProperty
    Instant executedAt;
    /* failedAt? and failureReason? */

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.INCOMING)
    Ship orderGivenTo;

    public Order() {
        this.executedAt = null;
    }

    @JsonProperty
    public abstract boolean isRepeatable();
}
