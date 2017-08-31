package com.voidaspect.public24.service.agent;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author mikhail.h
 */
@Value
@Builder
class SimpleMessageList {

    String header;

    String fallback;

    List<String> messages;

}
