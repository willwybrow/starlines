package uk.wycor.starlines.domain.ship;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.GameObject;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.star.Star;

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

    public Ship() {
    }

    public Ship(UUID id, Player owner, Star orbiting) {
        super(id);
        this.owner = owner;
        this.orbiting = orbiting;
    }
}
