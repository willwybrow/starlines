package uk.wycor.starlines.domain.order;

import org.junit.jupiter.api.Test;
import uk.wycor.starlines.domain.ship.order.OpenStarline;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MutualOpenStarlineTest {

    @Test
    void testEquals() {
        OpenStarline firstOrder = OpenStarline.builder().id(UUID.randomUUID()).build();
        OpenStarline secondOrder = OpenStarline.builder().id(UUID.randomUUID()).build();

        assertEquals(new MutualOpenStarline(firstOrder, secondOrder), new MutualOpenStarline(secondOrder, firstOrder));
    }
}