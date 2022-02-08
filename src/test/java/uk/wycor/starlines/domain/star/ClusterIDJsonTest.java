package uk.wycor.starlines.domain.star;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import uk.wycor.starlines.domain.JsonTest;
import uk.wycor.starlines.domain.star.ClusterID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusterIDJsonTest extends JsonTest {
    @Test
    void clusterIDCanBeSerialised() throws JsonProcessingException {
        assertEquals("{\"id\":3,\"coordinates\":{\"q\":-1,\"r\":-1,\"s\":2}}", objectMapper.writeValueAsString(new ClusterID(3)));
    }
}
