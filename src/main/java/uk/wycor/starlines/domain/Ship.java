package uk.wycor.starlines.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Ship extends GameObject {
    public Ship(UUID id) {
        super(id);
    }
    public Set<Order> validOrders() {
        return Arrays.stream(Order.values()).filter(order -> order.capableShips().contains(this.getClass())).collect(Collectors.toSet());
    }
}
