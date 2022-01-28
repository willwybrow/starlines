package uk.wycor.starlines.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Player extends GameObject {
    private final String name;

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
    }
}
