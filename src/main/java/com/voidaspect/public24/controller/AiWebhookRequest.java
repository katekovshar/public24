package com.voidaspect.public24.controller;

import ai.api.model.AIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for incoming requests
 * @author mikhail.h
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class AiWebhookRequest extends AIResponse {

    private static final long serialVersionUID = 1L;

    /**
     * Data of initial request to API.AI agent after NLP
     */
    private OriginalRequest originalRequest;

    /**
     * DTO for initial API.AI agent request
     */
    @Data
    public static final class OriginalRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        private String source;

        private Map<String, ?> data;
    }

}
