package com.voidaspect.public24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Incorrect webhook request")
public final class BadWebhookRequestException extends RuntimeException {

    public BadWebhookRequestException() {
        super();
    }

    public BadWebhookRequestException(String message) {
        super(message);
    }
}
