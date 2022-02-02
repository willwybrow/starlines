package uk.wycor.starlines.domain;

import java.time.Instant;

public class GivenOrder<T extends Ship> {
    Instant performByTick;
    Order order;
}
