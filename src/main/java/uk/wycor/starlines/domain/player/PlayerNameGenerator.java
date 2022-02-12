package uk.wycor.starlines.domain.player;

import uk.wycor.starlines.RandomSample;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerNameGenerator {
    private static final String[] INITIALS = {
            "B", "S", "W", "C", "Fr", "L"
    };
    private static final String[] FINALS = {
            "en", "am", "ill", "arol", "an", "ucy", "andy"
    };

    public static List<String> names() {
        return Arrays.stream(INITIALS).flatMap(i -> Arrays.stream(FINALS).map(f -> i + f)).collect(Collectors.toList());
    }

    public static String randomName() {
        return RandomSample.pick(names());
    }
}
