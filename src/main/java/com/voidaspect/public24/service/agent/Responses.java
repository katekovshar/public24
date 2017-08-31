package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
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
     * @param simpleMessageList contains list of strings used to populate response messages
     *                          and string used as fallback value in response if list of messages is insufficient
     * @return webhook response data
     */
    static Fulfillment fromSimpleStringList(SimpleMessageList simpleMessageList) {
        List<String> responseMessages;
        val messageListMessages = simpleMessageList.getMessages();
        if (messageListMessages.isEmpty()) {
            responseMessages = Collections.singletonList(simpleMessageList.getFallback());
        } else {
            responseMessages = new ArrayList<>(messageListMessages.size() + 1);
            responseMessages.add(simpleMessageList.getHeader());
            responseMessages.addAll(messageListMessages);
        }
        val fulfillment = new Fulfillment();
        List<ResponseMessage> responseSpeechList = responseMessages.stream()
                .map(m -> {
                    val responseSpeech = new ResponseMessage.ResponseSpeech();
                    responseSpeech.setSpeech(m);
                    return responseSpeech;
                })
                .collect(Collectors.toList());
        String speech = responseMessages.stream()
                .collect(Collectors.joining("\n"));
        fulfillment.setSpeech(speech);
        fulfillment.setMessages(responseSpeechList);
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

}
