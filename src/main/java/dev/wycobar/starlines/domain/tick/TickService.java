package dev.wycobar.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.GameState;
import dev.wycobar.starlines.persistence.neo4j.GameStateRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@Service
public class TickService {
    private static final int TICK_MINUTES = 5;
    private static final int MINUTE_SECONDS = 60;
    private static final int SECONDS_IN_HOUR = 60 * MINUTE_SECONDS;
    private static final int TICK_SECONDS = TICK_MINUTES * MINUTE_SECONDS;

    private final static Logger logger = Logger.getLogger(TickService.class.getName());

    private final Clock clock;
    private final GameStateRepository gameStateRepository;
    private final List<OnTickAction> onTickActions;

    @Autowired
    public TickService(Clock clock, GameStateRepository gameStateRepository, List<OnTickAction> onTickActions) {
        this.clock = clock;
        this.gameStateRepository = gameStateRepository;
        this.onTickActions = onTickActions;
        this.onTickActions.forEach(onTickAction -> logger.info(String.format("Registered OnTickAction handler %s", onTickAction.getClass().toString())));
    }

    public Instant nextTick() {
        Instant now = this.clock.instant();
        Instant startOfThisHour = now.truncatedTo(ChronoUnit.HOURS);
        return IntStream.range(0, (SECONDS_IN_HOUR / TICK_SECONDS) + 1)
                .map(i -> i * TICK_SECONDS)
                .mapToObj(startOfThisHour::plusSeconds)
                .filter(time -> time.isAfter(now))
                .findFirst()
                .orElseThrow(RuntimeException::new); // TODO: something better
    }

    private Instant previousTick() {
        return rollBackOneTick(nextTick());
    }

    private Instant rollBackOneTick(Instant tick) {
        return tick.minus(Duration.ofSeconds(TICK_SECONDS));
    }

    private Instant rollForwardOneTick(Instant tick) {
        return tick.plus(Duration.ofSeconds(TICK_SECONDS));
    }

    @Scheduled(fixedDelay = 10_000)
    public void executeTick() {
        logger.info("TICK!!!!");
        Instant previousTick = previousTick();
        checkAndExecuteTick(previousTick)
                .subscribe(
                        gameState -> logger.info(String.format("Processed tick %s", gameState.getExecutedTick())),
                        throwable -> {
                            logger.warning("Failed to execute tick");
                            logger.log(Level.SEVERE, "Fucked up trying to execute the tick", throwable);
                        }
                );
    }

    private Mono<GameState> checkAndExecuteTick(Instant previousTick) {
        return this
                .gameStateRepository
                .findById(GameState.GAME_STATE_ID)
                .switchIfEmpty(Mono.defer(() -> startGame(previousTick)))
                .flatMap(gameState -> {
                    logger.info(String.format("Most recently executed tick was %s. tick we just passed was %s", gameState.getExecutedTick(), previousTick));
                    if (previousTick.isAfter(gameState.getExecutedTick())) {
                        Instant tickToExecute = rollForwardOneTick(gameState.getExecutedTick());
                        return Flux.fromIterable(onTickActions)
                                .flatMap(onTickAction -> onTickAction.processActions(tickToExecute, rollForwardOneTick(tickToExecute)))
                                .then(markTickExecutionComplete(gameState, tickToExecute));
                    } else {
                        return gameStateRepository.findById(gameState.getId());
                    }
                });
    }

    private Mono<GameState> markTickExecutionComplete(GameState gameState, Instant executedTick) {
        return Mono.just(gameState)
                .map(gs -> gs.markAsExecuted(executedTick))
                .flatMap(gameStateRepository::save);
    }

    private Mono<GameState> startGame(Instant previousTick) {
        logger.info("Starting the game ticking...");
        return gameStateRepository.save(new GameState(GameState.GAME_STATE_ID, previousTick));
    }
}
