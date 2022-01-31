package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Data;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarControl;

import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Data
// @Accessors(fluent = true, chain = true)
public class StarJson {

    private String id;
    private String name;
    private HexPointJson coordinates;
    private int currentMass;
    private int maximumMass;

    private List<PlayerJson> controllingPlayers;
    private int controllingPlayerShipCount = 0;

    public static StarJson from(Star star) {
        return StarJson
                .builder()
                .id(star.getId().toString())
                .name(star.getName())
                .coordinates(HexPointJson.from(star.getCoordinate()))
                .currentMass(star.getCurrentMass())
                .maximumMass(star.getMaximumMass())
                .build();
    }

    public static StarJson from(StarControl starControl) {
        return StarJson
                .from(starControl.getStar())
                .toBuilder()
                .controllingPlayers(starControl
                        .getControllingPlayers()
                        .stream()
                        .map(PlayerJson::from)
                        .collect(Collectors.toList())
                )
                .controllingPlayerShipCount(starControl.getControllingProbeCount())
                .build();
    }
}
