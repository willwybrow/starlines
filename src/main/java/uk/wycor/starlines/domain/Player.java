package uk.wycor.starlines.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;


@Getter
@Setter
@Node("Player")
public class Player extends GameObject {
    private final String name;

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
    }
}
