package uk.wycor.starlines.web;

import io.vertx.core.json.Json;
import org.junit.jupiter.api.Test;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StarJsonTest {
    @Test
    void starSerialisation() {
        Star givenStar = new Star(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                new HexPoint(0, 0),
                "Star",
                1,
                1);

        StarJson starJson = StarJson.fromStar(givenStar);

        assertEquals(
                "{\"id\":\"00000000-0000-0000-0000-000000000000\",\"currentMass\":1,\"maximumMass\":1}",
                Json.encode(starJson)
        );
    }
}
