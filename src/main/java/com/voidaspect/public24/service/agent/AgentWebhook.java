package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.controller.AiWebhookRequest;

public interface AgentWebhook {

    Fulfillment fulfillAgentResponse(AiWebhookRequest aiWebhookRequest);

}
