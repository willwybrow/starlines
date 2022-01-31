package uk.wycor.starlines.domain;

import uk.wycor.starlines.RandomSample;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StarNameGenerator {

    private static final String[] INITIAL_CONSONANT_SOUNDS = {
            "B", "Bh", "Bj", "Bl", "Br", "Bw",
            "C", "Ch", "Chr", "Cl", "Cr", "Cw", "Cz",
            "D", "Dh", "Dr",
            "F", "Fl", "Fr",
            "G", "Gh", "Gl", "Gr",
            "H",
            "J", "Jh",
            "K", "Kh", "Khr", "Kl", "Kn", "Kr", "Kv", "Kw",
            "L", "Lh", "Ll",
            "M", "Mh", "Mn",
            "N", "Nh",
            "P", "Ph", "Phr", "Pr",
            "Q", "Qu",
            "S", "Sh", "Shr", "St", "Str", "Sr",
            "T", "Th", "Thr", "Tw",
            "V", "Vh", "Vr",
            "W", "Wh", "Wr",
            "X", "Xh", "Xy",
            "Y", "Yh",
            "Z", "Zh"
    };
    private static final String[] VOWEL_SOUNDS = {
            "a", "aa", "ae", "ai", "ao", "au",
            "e", "ea", "ee", "ei", "eo", "eu",
            "i", "ia", "ie", "ii", "io", "iu",
            "o", "oa", "oe", "oi", "oo", "ou",
            "u", "ua", "ue", "ui", "uo"
    };

    private static final String[] CONSONANT_SOUNDS = {
            "b", "bb",
            "c", "cc", "ch", "ck",
            "d", "dd",
            "f",
            "g", "gh", /*"gg", not this */
            "h",
            "j",
            "k",
            "l",
            "m", "mm",
            "n",
            "p", "pp",
            "r", "rr",
            "s", "ss",
            "t", "tt",
            "v",
            "w",
            "x",
            "z"
    };

    private static final String[][][] NAME_SCHEMAS = {
            {INITIAL_CONSONANT_SOUNDS, VOWEL_SOUNDS},
            {INITIAL_CONSONANT_SOUNDS, VOWEL_SOUNDS, CONSONANT_SOUNDS},
            {INITIAL_CONSONANT_SOUNDS, VOWEL_SOUNDS, CONSONANT_SOUNDS, VOWEL_SOUNDS},
    };

    public static String randomName() {
        return Arrays.stream(RandomSample.pick(Arrays.asList(NAME_SCHEMAS)))
                .map(sounds -> RandomSample.pick(Arrays.asList(sounds)))
                .collect(Collectors.joining());
    }
}
