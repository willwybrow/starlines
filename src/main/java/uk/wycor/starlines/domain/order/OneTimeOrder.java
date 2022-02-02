package uk.wycor.starlines.domain.order;

public abstract class OneTimeOrder extends Order {
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
