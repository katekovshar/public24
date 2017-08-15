package com.voidaspect.public24.service.agent.response;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for fulfillment objects
 */
@Service
public final class ResponseService implements ResponseFactory {

    /**
     * Source of data produced by webhook
     */
    private static final String SOURCE = "Privat24 API";

    /**
     * {@inheritDoc}
     */
    @Override
    public Fulfillment fromSimpleStringList(List<String> messages, String fallback) {
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
