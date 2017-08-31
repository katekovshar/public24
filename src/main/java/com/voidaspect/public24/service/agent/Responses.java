package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for fulfillment objects
 */
final class Responses {

    /**
     * Source of data produced by webhook
     */
    private static final String SOURCE = "Privat24 API";

    private Responses(){}

    /**
     * Creates {@link Fulfillment} object from a list of strings
     * @param messages list of strings used to populate response messages
     * @param fallback string used as fallback value in response if list of messages is insufficient
     * @return webhook response data
     */
    static Fulfillment fromSimpleStringList(List<String> messages, String fallback) {
        if (messages.size() <= 1) {
            messages.clear();
            messages.add(fallback);
        }
        val fulfillment = new Fulfillment();
        List<ResponseMessage> responseSpeechList = messages.stream()
                .map(m -> {
                    val responseSpeech = new ResponseMessage.ResponseSpeech();
                    responseSpeech.setSpeech(m);
                    return responseSpeech;
                })
                .collect(Collectors.toList());
        String speech = messages.stream()
                .collect(Collectors.joining("\n"));
        fulfillment.setSpeech(speech);
        fulfillment.setMessages(responseSpeechList);
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

}
