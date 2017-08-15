package com.voidaspect.public24.config.gson;

import ai.api.model.GoogleAssistantResponseMessages;
import ai.api.model.ResponseMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Configuration bean for Json-related configs
 *
 * @author mikhail.h
 */
@Configuration
public class GsonConfig {

    /**
     * Date format specification from API.AI {@link ai.api.GsonFactory}
     */
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    /**
     * Declares message converter used in this application
     *
     * @return message converter instance
     */
    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        val converter = new GsonHttpMessageConverter();
        converter.setGson(gson());
        return converter;
    }

    /**
     * Declares object mapper used in this application
     * @return object mapper instance
     * @see Adapters
     */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT.toPattern())
                .registerTypeAdapter(ResponseMessage.class, Adapters.getAdapter())
                .registerTypeAdapter(ResponseMessage.MessageType.class, Adapters.getAdapter())
                .registerTypeAdapter(ResponseMessage.Platform.class, Adapters.getAdapter())
                .registerTypeAdapter(ResponseMessage.ResponseSpeech.class, Adapters.getResponseSpeechAdapter())
                .registerTypeAdapter(GoogleAssistantResponseMessages.ResponseChatBubble.class, Adapters.getAdapter())
                .registerTypeAdapter(LocalDate.class, Adapters.getLocalDateAdapter(privat24DateFormat()))
                .create();
    }

    /**
     * Declares date format used in privat24 API
     * <br>For example: 04.10.2016
     * @return date format specification
     */
    @Bean
    public DateTimeFormatter privat24DateFormat() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

}
