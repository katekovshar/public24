package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import lombok.val;

import java.util.ArrayList;
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

    private Responses() {
    }

    /**
     * Creates {@link Fulfillment} object from a list of strings
     *
     * @param simpleMessageList contains list of strings used to populate response messages
     *                          and string used as fallback value in response if list of messages is insufficient
     * @return webhook response data
     */
    static Fulfillment fromSimpleStringList(SimpleMessageList simpleMessageList) {
        List<String> responseMessages;
        val messageListMessages = simpleMessageList.getMessages();

        if (messageListMessages.isEmpty())
            return fallback(simpleMessageList);

        responseMessages = new ArrayList<>(messageListMessages.size() + 1);
        responseMessages.add(simpleMessageList.getHeader());
        responseMessages.addAll(messageListMessages);
        val fulfillment = createFulfillmentStub();
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
        return fulfillment;
    }

    static Fulfillment fromMessageListWithLinks(MessageListWithLinks messageListWithLinks) {
        val messagesWithLinks = messageListWithLinks.getMessagesWithLinks();
        if (messagesWithLinks.isEmpty())
            return fallback(messageListWithLinks);

        val responseMessages = messagesWithLinks.entrySet().stream()
                .map(e -> new ResponseMessage.ResponseCard.Button(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
        val responseCard = new ResponseMessage.ResponseCard();
        responseCard.setTitle(messageListWithLinks.getHeader());
        responseCard.setButtons(responseMessages);

        val fulfillment = createFulfillmentStub();
        String speech = messagesWithLinks.keySet().stream()
                .collect(Collectors.joining("\n"));
        fulfillment.setMessages(responseCard);
        fulfillment.setSpeech(speech);
        return fulfillment;
    }

    private static Fulfillment fallback(ResponseMessageData responseMessageData) {
        val fulfillment = createFulfillmentStub();
        String speech = responseMessageData.getFallback();
        fulfillment.setDisplayText(speech);
        fulfillment.setSpeech(speech);
        return fulfillment;
    }

    private static Fulfillment createFulfillmentStub() {
        val fulfillment = new Fulfillment();
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

}
