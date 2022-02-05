package uk.wycor.starlines.persistence;

interface UnitOfWork<T> {
    T perform(GameRepository gameRepository);
}
