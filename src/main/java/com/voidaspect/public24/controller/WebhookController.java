package com.voidaspect.public24.controller;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.service.agent.AgentWebhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles POST requests from API.AI
 *
 * @author mikhail.h
 */
@RestController
@RequestMapping("/api-ai")
public final class WebhookController {

    /**
     * Service that handles requests.
     */
    private final AgentWebhook agentWebhook;

    /**
     * DI-managed constructor
     * 
     * @param agentWebhook value of {@link #agentWebhook}
     */
    @Autowired
    public WebhookController(AgentWebhook agentWebhook) {
        this.agentWebhook = agentWebhook;
    }

    /**
     * Webhook fulfillment endpoint.
     * Requires basic authentication.
     *
     * @param webhookRequest request data
     * @return webhook response data with speech and Rich Messages
     * @see com.voidaspect.public24.security.SecurityConfig
     */
    @PostMapping
    public Fulfillment fulfill(@RequestBody AiWebhookRequest webhookRequest) {
        return agentWebhook.fulfillAgentResponse(webhookRequest);
    }

}
