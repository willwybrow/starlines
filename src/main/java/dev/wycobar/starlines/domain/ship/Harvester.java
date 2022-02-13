package dev.wycobar.starlines.domain.ship;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.order.unit.BuildHarvester;
import dev.wycobar.starlines.domain.ship.order.unit.BuildProbe;
import dev.wycobar.starlines.domain.ship.order.unit.BuildStabiliser;
import dev.wycobar.starlines.domain.ship.order.starline.CloseStarlineOrder;
import dev.wycobar.starlines.domain.ship.order.harvest.Harvest;
import dev.wycobar.starlines.domain.star.Star;

import java.util.Set;
import java.util.UUID;

@Node("Harvester")
@Getter
@Setter
public class Harvester extends Ship {

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<BuildProbe> ordersToBuildProbe;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<BuildHarvester> ordersToBuildHarvester;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<BuildStabiliser> ordersToBuildStabiliser;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<Harvest> ordersToHarvest;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    Set<CloseStarlineOrder> ordersToCloseStarline;

    public Harvester() {
    }

    public Harvester(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.orbiting.getHarvestersInOrbit().add(this);
        this.owner.getOwnedHarvesters().add(this);
    }
}
