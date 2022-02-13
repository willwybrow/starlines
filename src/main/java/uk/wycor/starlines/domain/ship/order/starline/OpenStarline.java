package uk.wycor.starlines.domain.ship.order.starline;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.ship.order.OneTimeOrder;
import uk.wycor.starlines.domain.star.Star;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Node("OpenStarline")
public class OpenStarline extends OneTimeOrder {
    @Relationship(type = "TARGETING", direction = Relationship.Direction.OUTGOING)
    private Star target;

    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    @JsonProperty
    private UUID targetStarID() {
        return target.getId();
    }
}
