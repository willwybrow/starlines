package dev.wycobar.starlines.domain.ship.order;

public class ExecutionResult<T extends Order> {
    public enum Failure {
        CONFLICT
    }
}
