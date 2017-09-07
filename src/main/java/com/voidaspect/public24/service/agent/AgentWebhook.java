package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.controller.BadWebhookRequestException;

/**
 * Provides an API for handling requests from API.AI agent
 */
public interface AgentWebhook {

    /**
     * Handles webhook request via underlying services
     *
     * @param aiWebhookRequest request data
     * @return webhook response data with speech and Rich Messages
     * @throws BadWebhookRequestException when request intent is unrecognized or request is malformed
     */
    Fulfillment fulfillAgentResponse(AiWebhookRequest aiWebhookRequest) throws BadWebhookRequestException;

}
