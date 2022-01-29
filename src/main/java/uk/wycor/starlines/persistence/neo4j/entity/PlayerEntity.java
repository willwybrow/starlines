package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import uk.wycor.starlines.domain.Player;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "Player")
@Builder
public class PlayerEntity extends Entity {
    String name;

    public static PlayerEntity fromPlayer(Player player) {
        return PlayerEntity.builder().name(player.getName()).build();
    }
}
