package uk.wycor.starlines.persistence;

import uk.wycor.starlines.domain.GameRepository;

interface UnitOfWork<T> {
    T perform(GameRepository gameRepository);
}
