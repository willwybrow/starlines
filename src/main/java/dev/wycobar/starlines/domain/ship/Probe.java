package dev.wycobar.starlines.domain.ship;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.order.starline.CloseStarlineOrder;
import dev.wycobar.starlines.domain.ship.order.DeployHarvesterOrder;
import dev.wycobar.starlines.domain.ship.order.starline.OpenStarlineOrder;
import dev.wycobar.starlines.domain.star.Star;

import java.util.Set;
import java.util.UUID;

@Node("Probe")
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize
public class Probe extends Ship {

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<DeployHarvesterOrder> ordersToDeploy;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<OpenStarlineOrder> ordersToOpenStarline;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<CloseStarlineOrder> ordersToCloseStarline;

    public Probe(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.owner.getOwnedProbes().add(this);
        this.orbiting.getProbesInOrbit().add(this);
    }
}
