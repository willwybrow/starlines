package uk.wycor.starlines.domain.order;

import lombok.experimental.SuperBuilder;
import uk.wycor.starlines.domain.star.Star;

@SuperBuilder
public class OpenStarline extends OneTimeOrder {
    private Star target;
}
