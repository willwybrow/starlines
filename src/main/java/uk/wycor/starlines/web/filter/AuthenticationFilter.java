package uk.wycor.starlines.web.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.StarlinesGame;

import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Order(-10)
@Component
public class AuthenticationFilter implements WebFilter {
    public static final String AUTHENTICATED_USER_CONTEXT_KEY = "AUTHENTICATED_USER";

    private final StarlinesGame starlinesGame;

    @Autowired
    public AuthenticationFilter(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // placeholder, test works good, replace with bearer tokens or something else fun
        Player player = extractPlayerIDFromAuthorizationHeader(exchange);
        if (player == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().put(HttpHeaders.WWW_AUTHENTICATE, Collections.singletonList("Basic"));
            return exchange.getResponse().setComplete();
        }
        return starlinesGame.loadOrCreatePlayer(player)
                .flatMap(p -> chain
                        .filter(exchange)
                        .contextWrite(Context.of(AUTHENTICATED_USER_CONTEXT_KEY, p))
                );
    }

    private Player extractPlayerIDFromAuthorizationHeader(ServerWebExchange exchange) {
        try {
            return Optional
                    .ofNullable(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                    .map(headerValue -> headerValue.split("\s+", 2)[1])
                    .map(userPassBase64 -> Base64.getDecoder().decode(userPassBase64))
                    .map(String::new)
                    .map(userPass -> userPass.split(":", 2))
                    .map(userPass -> new Player(UUID.fromString(userPass[1]), userPass[0]))
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
