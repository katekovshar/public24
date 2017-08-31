package com.voidaspect.public24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception which signifies incorrect or incomplete requests
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public final class BadWebhookRequestException extends RuntimeException {

    public BadWebhookRequestException(String message) {
        super(message);
    }
}
