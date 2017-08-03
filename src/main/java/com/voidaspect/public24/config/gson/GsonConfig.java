package com.voidaspect.public24.config.gson;

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
 * @author mikhail.h
 */
@Configuration
public class GsonConfig {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        val converter = new GsonHttpMessageConverter();
        converter.setGson(gson());
        return converter;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT.toPattern())
                .registerTypeAdapter(ResponseMessage.class, Adapters.getResponseMessageAdapter())
                .registerTypeAdapter(ResponseMessage.MessageType.class, Adapters.getResponseMessageTypeAdapter())
                .registerTypeAdapter(ResponseMessage.Platform.class, Adapters.getResponseMessagePlatformAdapter())
                .registerTypeAdapter(ResponseMessage.ResponseSpeech.class, Adapters.getResponseMessageSpeechDeserializer())
                .registerTypeAdapter(LocalDate.class, Adapters.getLocalDateAdapter(privat24DateFormat()))
                .create();
    }

    @Bean
    public DateTimeFormatter privat24DateFormat() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

}
