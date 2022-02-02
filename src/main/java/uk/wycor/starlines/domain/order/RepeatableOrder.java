package uk.wycor.starlines.domain.order;

public abstract class RepeatableOrder extends Order {
    @Override
    public boolean isRepeatable() {
        return true;
    }
}
