package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarControl;

import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
@Setter
// @Accessors(fluent = true, chain = true)
public class StarJson {

    private String id;
    private String name;
    private HexPointJson coordinates;
    private long currentMass;
    private long maximumMass;

    private List<PlayerJson> controllingPlayers;
    private long controllingPlayerProbeCount;

    public static StarJson from(Star star) {
        return StarJson
                .builder()
                .id(star.getId().toString())
                .name(star.getName())
                .coordinates(HexPointJson.from(star.getCoordinate()))
                .currentMass(star.getCurrentMass())
                .maximumMass(star.getNaturalMassCapacity())
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
                .controllingPlayerProbeCount(starControl.getControllingProbeCount())
                .build();
    }
}
