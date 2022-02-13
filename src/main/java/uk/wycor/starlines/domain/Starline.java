package uk.wycor.starlines.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;
import uk.wycor.starlines.domain.star.Star;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@Node("Starline")
public class Starline extends GameObject {
    private Set<StarlineSpan> network;

    public Set<Star> starsInStarline() {
        return Optional
                .ofNullable(network)
                .stream()
                .flatMap(Set::stream)
                .map(StarlineSpan::getStar)
                .collect(Collectors.toSet());
    }

    public Starline(UUID id, Set<StarlineSpan> network) {
        super(id);
        this.network = network;
    }
}
