package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Node("Player")
public class Player extends GameObject {
    @JsonProperty
    private String name;

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
    }
}
