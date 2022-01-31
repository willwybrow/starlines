package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;
import uk.wycor.starlines.domain.Player;

@Getter
// @Accessors(fluent = true, chain = true)
@Builder
public class PlayerJson {
    String id;
    String name;

    public static PlayerJson from(Player player) {
        return PlayerJson.builder().id(player.getId().toString()).name(player.getName()).build();
    }
}
