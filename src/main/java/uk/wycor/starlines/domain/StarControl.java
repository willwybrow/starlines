package uk.wycor.starlines.domain;

import lombok.Getter;

import java.util.List;

public class StarControl {
    @Getter
    private final Star star;
    @Getter
    private final List<Player> controllingPlayers;
    @Getter
    private final int controllingProbeCount;

    public StarControl(Star star, List<Player> controllingPlayers, int controllingProbeCount) {
        this.star = star;
        this.controllingPlayers = controllingPlayers;
        this.controllingProbeCount = controllingProbeCount;
    }
}
