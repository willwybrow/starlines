package dev.wycobar.starlines.domain.order;

import org.junit.jupiter.api.Test;
import dev.wycobar.starlines.domain.ship.order.starline.OpenStarlineOrder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MutualOpenStarlineOrderTest {

    @Test
    void testEquals() {
        OpenStarlineOrder firstOrder = OpenStarlineOrder.builder().id(UUID.randomUUID()).build();
        OpenStarlineOrder secondOrder = OpenStarlineOrder.builder().id(UUID.randomUUID()).build();

        assertEquals(new MutualOpenStarline(firstOrder, secondOrder), new MutualOpenStarline(secondOrder, firstOrder));
    }
}