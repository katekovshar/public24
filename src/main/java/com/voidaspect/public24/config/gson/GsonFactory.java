package com.voidaspect.public24.config.gson;

import ai.api.model.ResponseMessage;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author mikhail.h
 */
public final class GsonFactory {

    private static final Gson DEFAULT_GSON = new GsonBuilder().create();

    private static final GsonBuilder PROTOCOL_GSON = new GsonBuilder()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).toPattern())
            .registerTypeAdapter(ResponseMessage.class, new GsonFactory.ResponseItemAdapter())
            .registerTypeAdapter(ResponseMessage.MessageType.class, new GsonFactory.ResponseMessageTypeAdapter())
            .registerTypeAdapter(ResponseMessage.Platform.class, new GsonFactory.ResponseMessagePlatformAdapter())
            .registerTypeAdapter(ResponseMessage.ResponseSpeech.class, new GsonFactory.ResponseSpeechDeserializer());

    private static final GsonFactory DEFAULT_FACTORY = new GsonFactory();

    public GsonBuilder getGsonBuilder() {
        return PROTOCOL_GSON;
    }

    /**
     * Create a default factory
     */
    public static GsonFactory getDefaultFactory() {
        return DEFAULT_FACTORY;
    }

    private static class ResponseMessagePlatformAdapter implements
            JsonDeserializer<ResponseMessage.Platform>,
            JsonSerializer<ResponseMessage.Platform> {

        @Override
        public JsonElement serialize(ResponseMessage.Platform src, Type typeOfT, JsonSerializationContext context) {
            return context.serialize(src.getName());
        }

        @Override
        public ResponseMessage.Platform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String name = json.getAsString();
            if (name == null) {
                return ResponseMessage.Platform.DEFAULT;
            }
            ResponseMessage.Platform result = ResponseMessage.Platform.fromName(name);
            if (result == null) {
                throw new JsonParseException(String.format("Unexpected platform name: %s", json));
            }
            return result;
        }
    }
    private static class ResponseMessageTypeAdapter implements
            JsonDeserializer<ResponseMessage.MessageType>,
            JsonSerializer<ResponseMessage.MessageType> {

        @Override
        public JsonElement serialize(ResponseMessage.MessageType src, Type typeOfT, JsonSerializationContext context) {
            return context.serialize(src.getCode() <= 4 ? src.getCode() : src.getName());
        }

        @Override
        public ResponseMessage.MessageType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonPrimitive jsonValue = json.getAsJsonPrimitive();
            ResponseMessage.MessageType result;
            if (jsonValue.isNumber()) {
                result = ResponseMessage.MessageType.fromCode(jsonValue.getAsInt());
            } else {
                result = ResponseMessage.MessageType.fromName(jsonValue.getAsString());
            }
            if (result == null) {
                throw new JsonParseException(String.format("Unexpected message type value: %s", jsonValue));
            }
            return result;
        }
    }

    private static class ResponseItemAdapter implements JsonDeserializer<ResponseMessage>,
            JsonSerializer<ResponseMessage> {

        @Override
        public ResponseMessage deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {
            ResponseMessage.MessageType messageType = context.deserialize(json.getAsJsonObject().get("type"), ResponseMessage.MessageType.class);
            return context.deserialize(json, messageType.getType());
        }

        @Override
        public JsonElement serialize(ResponseMessage src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            return context.serialize(src, src.getClass());
        }
    }

    private static class ResponseSpeechDeserializer implements JsonDeserializer<ResponseMessage> {
        public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT,
                                                          JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonObject() && ((JsonObject) json).get("speech").isJsonPrimitive()) {
                JsonArray array = new JsonArray();
                array.add(((JsonObject) json).get("speech"));
                ((JsonObject) json).add("speech", array);
            }

            return DEFAULT_GSON.fromJson(json, typeOfT);
        }
    }
}
