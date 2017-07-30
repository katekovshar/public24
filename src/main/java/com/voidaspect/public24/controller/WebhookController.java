package com.voidaspect.public24.controller;

import ai.api.model.Fulfillment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikhail.h
 */
@RestController
@RequestMapping("/api-ai")
public final class WebhookController {

    @PostMapping
    public Fulfillment fulfill(@RequestBody AiWebhookRequest webhookRequest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

}
