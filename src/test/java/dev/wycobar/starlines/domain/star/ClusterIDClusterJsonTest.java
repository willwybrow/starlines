package dev.wycobar.starlines.domain.star;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import dev.wycobar.starlines.domain.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusterIDClusterJsonTest extends JsonTest {
    @Test
    void clusterIDCanBeSerialised() throws JsonProcessingException {
        assertEquals(
                "{\"id\":3,\"coordinates\":{\"q\":-1,\"r\":-1,\"s\":2},\"neighbours\":[{\"id\":9,\"coordinates\":{\"q\":0,\"r\":-2,\"s\":2}},{\"id\":1,\"coordinates\":{\"q\":0,\"r\":-1,\"s\":1}},{\"id\":2,\"coordinates\":{\"q\":-1,\"r\":0,\"s\":1}},{\"id\":12,\"coordinates\":{\"q\":-2,\"r\":0,\"s\":2}},{\"id\":13,\"coordinates\":{\"q\":-2,\"r\":-1,\"s\":3}},{\"id\":10,\"coordinates\":{\"q\":-1,\"r\":-2,\"s\":3}}]}",
                objectMapper.writeValueAsString(new ClusterIDCluster(3))
        );
    }
}
