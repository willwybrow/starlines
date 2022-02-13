package dev.wycobar.starlines.domain;

import lombok.Data;

@Data
public class Message {
    GameObject regarding;
    String event;
}
