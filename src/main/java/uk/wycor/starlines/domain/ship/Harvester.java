package uk.wycor.starlines.domain.ship;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.player.Player;
import uk.wycor.starlines.domain.ship.order.BuildHarvester;
import uk.wycor.starlines.domain.ship.order.BuildProbe;
import uk.wycor.starlines.domain.ship.order.BuildStabiliser;
import uk.wycor.starlines.domain.ship.order.Harvest;
import uk.wycor.starlines.domain.star.Star;

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

    public Harvester() {
    }

    public Harvester(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.orbiting.getHarvestersInOrbit().add(this);
        this.owner.getOwnedHarvesters().add(this);
    }
}
