package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.Color;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GameObject {
    private final UUID id;
}
