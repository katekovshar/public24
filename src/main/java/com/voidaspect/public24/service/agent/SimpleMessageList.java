package com.voidaspect.public24.service.agent;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

/**
 * @author mikhail.h
 */
@EqualsAndHashCode(callSuper = true)
@Value
class SimpleMessageList extends MessageList {

    List<String> messages;

    @Builder
    SimpleMessageList(List<String> messages, String header, String fallback) {
        super(header, fallback);
        this.messages = messages;
    }
}
