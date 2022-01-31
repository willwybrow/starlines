package uk.wycor.starlines.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipTest {
    @Test
    void validOrdersForShip() {
        Probe probe = new Probe(UUID.randomUUID());

        assertEquals(Set.of(Order.OPEN_STARLINE, Order.CLOSE_STARLINE), probe.validOrders());
    }
}