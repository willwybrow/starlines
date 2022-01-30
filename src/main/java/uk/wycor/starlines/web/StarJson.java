package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Data;
import uk.wycor.starlines.domain.Star;

@Builder
@Data
public class StarJson {

    private String id;
    private String name;
    private HexPointJson coordinates;
    private int currentMass;
    private int maximumMass;

    public static StarJson fromStar(Star star) {
        return StarJson
                .builder()
                .id(star.getId().toString())
                .name(star.getName())
                .coordinates(HexPointJson.from(star.getCoordinate()))
                .currentMass(star.getCurrentMass())
                .maximumMass(star.getMaximumMass())
                .build();
    }
}
