package dev.wycobar.starlines.domain.ship;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import dev.wycobar.starlines.domain.GameObject;
import dev.wycobar.starlines.domain.player.Player;
import dev.wycobar.starlines.domain.ship.order.Order;
import dev.wycobar.starlines.domain.star.Star;

import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@JsonInclude(value = NON_NULL)
@Node("Ship")
public abstract class Ship extends GameObject {
    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.OUTGOING)
    @JsonProperty("owner")
    protected Player owner;

    @Relationship(type = "ORBITING", direction = Relationship.Direction.OUTGOING)
    @JsonManagedReference
    protected Star orbiting;

    @Relationship(type = "ORDERED_TO", direction = Relationship.Direction.OUTGOING)
    @JsonProperty
    Set<Order> orders;

    public Ship() {
    }

    public Ship(UUID id, Player owner, Star orbiting) {
        super(id);
        this.owner = owner;
        this.orbiting = orbiting;
    }
}
