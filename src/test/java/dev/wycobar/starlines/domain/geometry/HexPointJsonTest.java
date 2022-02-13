package dev.wycobar.starlines.domain.geometry;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import dev.wycobar.starlines.domain.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexPointJsonTest extends JsonTest {
    @Test
    void hexPointSerialisesToJson() throws JsonProcessingException {
        assertEquals("{\"q\":2,\"r\":3,\"s\":-5}", objectMapper.writeValueAsString(new HexPoint(2, 3)));
    }
}
