package uk.wycor.starlines.domain.ship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.domain.star.Star;

import java.util.Set;
import java.util.UUID;

@Node("Probe")
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize
public class Probe extends Ship {

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    @JsonProperty
    Set<EstablishSelfAsHarvester> ordersToEstablish;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    @JsonProperty
    Set<Order> orders;

    public Probe(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.owner.getOwnedProbes().add(this);
        this.orbiting.getProbesInOrbit().add(this);
    }
}
