package uk.wycor.starlines.domain;

import lombok.Data;

@Data
public class Message {
    GameObject regarding;
    String event;
}
