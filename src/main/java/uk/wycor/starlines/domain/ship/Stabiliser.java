package uk.wycor.starlines.domain.ship;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.star.Star;

import java.util.UUID;

@Node("Stabiliser")
@Getter
@Setter
public class Stabiliser extends Ship {
    public Stabiliser(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
        this.orbiting.getStabilisersInOrbit().add(this);
    }

    public Stabiliser() {
    }
}
