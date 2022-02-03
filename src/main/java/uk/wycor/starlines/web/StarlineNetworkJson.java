package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class StarlineNetworkJson {
    List<StarlineJson> starlines;
}
