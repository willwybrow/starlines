package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Getter
@Setter
//@Node("Ship")
public abstract class Ship extends GameObject {
    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.OUTGOING)
    @JsonProperty("owner")
    private Player owner;

    @Relationship(type = "ORBITING", direction = Relationship.Direction.OUTGOING)
    private Star orbiting;

    public Ship(UUID id, Player owner, Star orbiting) {
        super(id);
        this.owner = owner;
        this.orbiting = orbiting;
    }
}
