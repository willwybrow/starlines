package uk.wycor.starlines.web.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.StarlinesGame;

@Component
public class NextTickHeaderFilter implements WebFilter {
    private final StarlinesGame starlinesGame;

    private final static String NEXT_TICK_HEADER_NAME = "Next-Tick";

    @Autowired
    public NextTickHeaderFilter(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().set(NEXT_TICK_HEADER_NAME, starlinesGame.nextTick().toString());
        return chain
                .filter(exchange);
    }
}
