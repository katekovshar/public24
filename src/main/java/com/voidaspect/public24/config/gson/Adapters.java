package com.voidaspect.public24.config.gson;

import ai.api.GsonFactory;
import ai.api.model.ResponseMessage;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author mikhail.h
 */
final class Adapters {

    private static final Gson DEFAULT_GSON = GsonFactory.getDefaultFactory().getGson();

    /**
     * Disallow instantiation
     */
    private Adapters(){
    }

    static abstract class TypeSerializationAdapter<T> implements
            JsonDeserializer<T>,
            JsonSerializer<T> {
    }

    static <T> ApiAiSerializationAdapter<T> getAdapter() {
        return new ApiAiSerializationAdapter<>();
    }


    static TypeSerializationAdapter<ResponseMessage.ResponseSpeech> getResponseSpeechAdapter() {
        return new ResponseSpeechAdapter();
    }

    static TypeSerializationAdapter<LocalDate> getLocalDateAdapter(DateTimeFormatter formatter) {
        return new LocalDateAdapter(formatter);
    }

    private static final class ApiAiSerializationAdapter<T> extends TypeSerializationAdapter<T> {

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return DEFAULT_GSON.fromJson(json, typeOfT);
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return DEFAULT_GSON.toJsonTree(src, typeOfSrc);
        }
    }
    private static class ResponseSpeechAdapter extends TypeSerializationAdapter<ResponseMessage.ResponseSpeech> {

        public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT,
                                                          JsonDeserializationContext context) throws JsonParseException {

            return DEFAULT_GSON.fromJson(json, typeOfT);
        }

        @Override
        public JsonElement serialize(ResponseMessage.ResponseSpeech src, Type typeOfSrc, JsonSerializationContext context) {

            JsonElement json = DEFAULT_GSON.toJsonTree(src, ResponseMessage.class);
            JsonObject asJsonObject = json.getAsJsonObject();
            JsonArray speech = asJsonObject.getAsJsonArray("speech");
            if (speech.size() == 1) {
                asJsonObject.addProperty("speech", speech.get(0).getAsString());
            }
            return json;
        }
    }

    /**
     * @author mikhail.h
     */
    private static final class LocalDateAdapter extends TypeSerializationAdapter<LocalDate> {

        private final DateTimeFormatter dateTimeFormatter;

        private LocalDateAdapter(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
        }

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.format(dateTimeFormatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), dateTimeFormatter);
        }
    }
}
