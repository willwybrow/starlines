package uk.wycor.starlines.persistence;

import uk.wycor.starlines.domain.GameObject;

import java.util.function.Predicate;

public interface Specification<T extends GameObject> extends Predicate<T> {

}
