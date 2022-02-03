package uk.wycor.starlines.domain;

import lombok.Getter;

import java.util.Collection;

public class StarControl {
    @Getter
    private final Star star;
    @Getter
    private final Collection<Player> controllingPlayers;
    @Getter
    private final long controllingProbeCount;

    public StarControl(Star star, Collection<Player> controllingPlayers, long controllingProbeCount) {
        this.star = star;
        this.controllingPlayers = controllingPlayers;
        this.controllingProbeCount = controllingProbeCount;
    }
}
