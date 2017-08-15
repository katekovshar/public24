package com.voidaspect.public24.service.agent.response;

import ai.api.model.Fulfillment;
import lombok.NonNull;

import java.util.List;

/**
 * Factory for fulfillment objects
 */
public interface ResponseFactory {

    /**
     * Creates {@link Fulfillment} object from a list of strings
     * @param messages list of strings used to populate response messages
     * @param fallback string used as fallback value in response if list of messages is insufficient
     * @return webhook response data
     */
    Fulfillment fromSimpleStringList(List<String> messages, @NonNull String fallback);

}
