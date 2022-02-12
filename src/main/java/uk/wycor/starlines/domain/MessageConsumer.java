package uk.wycor.starlines.domain;

public interface MessageConsumer {
    void handle(Message message);
}
