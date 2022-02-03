package uk.wycor.starlines.domain.order;

import lombok.Builder;
import uk.wycor.starlines.domain.Star;

@Builder
public class OpenStarline extends OneTimeOrder {
    private Star target;
}
