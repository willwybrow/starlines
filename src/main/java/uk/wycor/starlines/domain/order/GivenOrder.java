package uk.wycor.starlines.domain.order;

import lombok.Builder;
import lombok.Getter;
import uk.wycor.starlines.domain.ship.Ship;

import java.time.Instant;

@Builder
@Getter
public class GivenOrder<T extends Ship> {
    private final Instant performByTick;
    private final Order order;
}
