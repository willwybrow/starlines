package uk.wycor.starlines.domain.tick;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Service
public interface OnTickAction {
    Flux<Void> processActions(Instant thisTick, Instant nextTick);
}
