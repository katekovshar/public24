package com.voidaspect.public24.controller;

import ai.api.model.AIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * @author mikhail.h
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class AiWebhookRequest extends AIResponse {

    private static final long serialVersionUID = 1L;

    private OriginalRequest originalRequest;

    @Data
    public static final class OriginalRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        private String source;

        private Map<String, ?> data;
    }

}
