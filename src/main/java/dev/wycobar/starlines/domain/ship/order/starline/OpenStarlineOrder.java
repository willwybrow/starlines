package dev.wycobar.starlines.domain.ship.order.starline;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.wycobar.starlines.domain.ship.order.OneTimeOrder;
import dev.wycobar.starlines.domain.star.Star;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Node("OpenStarline")
public class OpenStarlineOrder extends OneTimeOrder {
    @Relationship(type = "TARGETING", direction = Relationship.Direction.OUTGOING)
    private Star target;

    @JsonProperty
    private Map<String, String> targetStar() {
        return Map.of("id", target.getId().toString(), "name", target.getName());
    }
}
