package dev.wycobar.starlines.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageBus {
    // private final Queue<Message> messages = new ConcurrentLinkedQueue<>();
    private final List<MessageConsumer> consumers = new ArrayList<>();

    public void publish(Message message) {
        consumers.forEach(messageConsumer -> messageConsumer.handle(message));
    }

    public void subscribe(MessageConsumer messageConsumer) {
        this.consumers.add(messageConsumer);
    }
}
