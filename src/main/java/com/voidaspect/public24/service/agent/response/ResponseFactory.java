package com.voidaspect.public24.service.agent.response;

import ai.api.model.Fulfillment;

import java.util.List;

public interface ResponseFactory {

    Fulfillment fromSimpleStringList(List<String> messages, String fallback);

}
