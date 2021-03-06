package dev.wycobar.starlines.domain.star;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import dev.wycobar.starlines.domain.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusterIDJsonTest extends JsonTest {
    @Test
    void clusterIDCanBeSerialised() throws JsonProcessingException {
        assertEquals("{\"id\":3,\"coordinates\":{\"q\":-1,\"r\":-1,\"s\":2}}", objectMapper.writeValueAsString(new ClusterID(3)));
    }
}
