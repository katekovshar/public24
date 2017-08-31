package com.voidaspect.public24.service.agent;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.net.URI;
import java.util.Map;

/**
 * @author mikhail.h
 */
@EqualsAndHashCode(callSuper = true)
@Value
final class MessageListWithLinks extends ResponseMessageData {

    Map<String, URI> messagesWithLinks;

    @Builder
    MessageListWithLinks(Map<String, URI> messagesWithLinks, String header, String fallback) {
        super(header, fallback);
        this.messagesWithLinks = messagesWithLinks;
    }

}
