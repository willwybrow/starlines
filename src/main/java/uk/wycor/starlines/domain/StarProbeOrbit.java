package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class StarProbeOrbit {
    private Star star;
    private Set<Probe> orbitingProbes;
}
