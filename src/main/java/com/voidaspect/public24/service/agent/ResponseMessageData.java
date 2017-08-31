package com.voidaspect.public24.service.agent;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author mikhail.h
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
abstract class ResponseMessageData {

    private final String header;

    private final String fallback;

}
