package uk.wycor.starlines.domain.order;

import uk.wycor.starlines.domain.Ship;

import java.time.Instant;

public class GivenOrder<T extends Ship> {
    Instant performByTick;
    Order order;
}
