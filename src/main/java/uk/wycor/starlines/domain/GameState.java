package uk.wycor.starlines.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.Instant;
import java.util.UUID;

@Node("GameState")
@Getter
@Setter
@NoArgsConstructor
public class GameState extends GameObject {
    public static final UUID GAME_STATE_ID = UUID.fromString("c1731d11-dab3-48cb-b809-25c3460efd42");

    private Instant executedTick;

    public GameState(UUID id, Instant executedTick) {
        super(id);
        this.executedTick = executedTick;
    }

    public GameState markAsExecuted(Instant executedTick) {
        this.executedTick = executedTick;
        return this;
    }
}
