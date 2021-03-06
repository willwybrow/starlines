package dev.wycobar.starlines.domain.star;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import dev.wycobar.starlines.domain.player.Player;

import java.util.Collection;

public class StarControl {
    @Getter
    @JsonProperty
    private final Collection<Player> players;
    @Getter
    @JsonProperty
    private final long probeCount;

    public StarControl(Collection<Player> players, long probeCount) {
        this.players = players;
        this.probeCount = probeCount;
    }
}
