package com.voidaspect.public24.config.gson;

import ai.api.GsonFactory;
import ai.api.model.ResponseMessage;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Static factory for Gson type adapters employed by this application,
 * including those used in API.AI library
 *
 * @author mikhail.h
 */
final class Adapters {

    /**
     * Instance of Gson object mapper configured in API.AI library
     */
    private static final Gson DEFAULT_GSON = GsonFactory.getDefaultFactory().getGson();

    /**
     * Disallow instantiation
     */
    private Adapters(){
    }

    /**
     * Abstract facade for serializer and deserializer
     * @param <T> type of an object to be serialized
     */
    static abstract class TypeSerializationAdapter<T> implements
            JsonDeserializer<T>,
            JsonSerializer<T> {
    }

    /**
     * Factory method that creates an adapter for given type
     * @param <T> type of an objects o be serialized
     * @return generic adapter for API.AI data
     */
    static <T> ApiAiSerializationAdapter<T> getAdapter() {
        return new ApiAiSerializationAdapter<>();
    }

    /**
     * Factory method that creates adapter for {@link ai.api.model.ResponseMessage.ResponseSpeech}.
     * @return custom type adapter
     */
    static TypeSerializationAdapter<ResponseMessage.ResponseSpeech> getResponseSpeechAdapter() {
        return new ResponseSpeechAdapter();
    }

    /**
     * Factory method that creates adaptor for {@link LocalDate}
     * @param formatter date format specification
     * @return custom type adapter
     */
    static TypeSerializationAdapter<LocalDate> getLocalDateAdapter(DateTimeFormatter formatter) {
        return new LocalDateAdapter(formatter);
    }

    /**
     * Generic type adapter for API.AI data. Relies on {@link #DEFAULT_GSON}.
     * @param <T> type of an object to be serialized
     * @see GsonFactory
     */
    private static final class ApiAiSerializationAdapter<T> extends TypeSerializationAdapter<T> {

        /**
         * {@inheritDoc}
         */
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return DEFAULT_GSON.fromJson(json, typeOfT);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return DEFAULT_GSON.toJsonTree(src, typeOfSrc);
        }
    }

    /**
     * Custom type adapter for API.AI {@link ai.api.model.ResponseMessage.ResponseSpeech}
     * Provides a workaround for {@link ResponseMessage} serialization
     */
    private static class ResponseSpeechAdapter extends TypeSerializationAdapter<ResponseMessage.ResponseSpeech> {

        /**
         * {@inheritDoc}
         */
        @Override
        public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT,
                                                          JsonDeserializationContext context) throws JsonParseException {

            return DEFAULT_GSON.fromJson(json, typeOfT);
        }

        /**
         * Turns {@link ai.api.model.ResponseMessage.ResponseSpeech#speech} array into single string value
         */
        @Override
        public JsonElement serialize(ResponseMessage.ResponseSpeech src, Type typeOfSrc, JsonSerializationContext context) {

            JsonElement json = DEFAULT_GSON.toJsonTree(src, ResponseMessage.class);
            JsonObject asJsonObject = json.getAsJsonObject();
            JsonArray speech = asJsonObject.getAsJsonArray("speech");
            if (speech.size() == 1) { //this shouldn't be an array
                asJsonObject.addProperty("speech", speech.get(0).getAsString());
            }
            return json;
        }
    }

    /**
     * Custom type adapter for {@link LocalDate} which allows to specify format
     * @author mikhail.h
     */
    @RequiredArgsConstructor
    private static final class LocalDateAdapter extends TypeSerializationAdapter<LocalDate> {

        /**
         * Format specification used in serialisation
         */
        private final DateTimeFormatter dateTimeFormatter;

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.format(dateTimeFormatter));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), dateTimeFormatter);
        }
    }
}
