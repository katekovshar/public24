package com.voidaspect.public24.controller;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.service.agent.AgentWebhook;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final AgentWebhook agentWebhook;

    @Autowired
    public WebhookController(AgentWebhook agentWebhook) {
        this.agentWebhook = agentWebhook;
    }

    @PostMapping
    public Fulfillment fulfill(@RequestBody AiWebhookRequest webhookRequest) {
        return agentWebhook.fulfillAgentResponse(webhookRequest);
    }

}
