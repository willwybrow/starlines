package dev.wycobar.starlines.domain;

public interface MessageConsumer {
    void handle(Message message);
}
