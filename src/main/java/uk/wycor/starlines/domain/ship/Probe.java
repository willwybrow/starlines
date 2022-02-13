package uk.wycor.starlines.domain.ship;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.player.Player;
import uk.wycor.starlines.domain.ship.order.CloseStarline;
import uk.wycor.starlines.domain.ship.order.EstablishSelfAsHarvester;
import uk.wycor.starlines.domain.ship.order.OpenStarline;
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
    Set<EstablishSelfAsHarvester> ordersToEstablish;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<OpenStarline> ordersToOpenStarline;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<CloseStarline> ordersToCloseStarline;

    public Probe(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.owner.getOwnedProbes().add(this);
        this.orbiting.getProbesInOrbit().add(this);
    }
}
