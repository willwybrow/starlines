package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.persistence.neo4j.StarlineRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@Service
public class StarlinesGame {
    private final static Logger logger = Logger.getLogger(StarlinesGame.class.getName());

    private final Clock clock;
    public final UniverseService universeService;
    private final PlayerService playerService;
    private final StarRepository starRepository;
    private final PlayerRepository playerRepository;
    private final StarlineRepository starlineRepository;
    private final ProbeRepository probeRepository;

    @Autowired
    public StarlinesGame(Clock clock, UniverseService universeService, PlayerService playerService, StarRepository starRepository, PlayerRepository playerRepository, StarlineRepository starlineRepository, ProbeRepository probeRepository) {
        this.clock = clock;
        this.universeService = universeService;
        this.playerService = playerService;
        this.starRepository = starRepository;
        this.playerRepository = playerRepository;
        this.starlineRepository = starlineRepository;
        this.probeRepository = probeRepository;
    }

    public Instant nextTick() {
        LocalDateTime now = LocalDateTime.ofInstant(this.clock.instant(), ZoneOffset.UTC);
        LocalDateTime startOfThisHour = now.truncatedTo(ChronoUnit.HOURS);
        return IntStream.range(0, 5)
                .map(i -> i * 15)
                .mapToObj(startOfThisHour::plusMinutes)
                .filter(time -> time.isAfter(now))
                .findFirst()
                .orElseThrow(RuntimeException::new) // TODO: something better
                .toInstant(ZoneOffset.UTC);
    }
}
