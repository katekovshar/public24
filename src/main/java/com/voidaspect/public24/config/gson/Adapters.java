package com.voidaspect.public24.config.gson;

import ai.api.model.ResponseMessage;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author mikhail.h
 */
final class Adapters {

    private static final Gson DEFAULT_GSON = new GsonBuilder().create();

    /**
     * Disallow instantiation
     */
    private Adapters(){
    }

    static abstract class TypeSerializationAdapter<T> implements
            JsonDeserializer<T>,
            JsonSerializer<T> {
    }

    static TypeSerializationAdapter<LocalDate> getLocalDateAdapter(DateTimeFormatter dateTimeFormatter) {
        return new LocalDateAdapter(dateTimeFormatter);
    }

    static TypeSerializationAdapter<ResponseMessage.Platform> getResponseMessagePlatformAdapter() {
        return RESPONSE_MESSAGE_PLATFORM;
    }

    static TypeSerializationAdapter<ResponseMessage.MessageType> getResponseMessageTypeAdapter() {
        return RESPONSE_MESSAGE_TYPE;
    }

    static TypeSerializationAdapter<ResponseMessage> getResponseMessageAdapter() {
        return RESPONSE_MESSAGE;
    }

    static JsonDeserializer<ResponseMessage.ResponseSpeech> getResponseMessageSpeechDeserializer() {
        return RESPONSE_MESSAGE_SPEECH;
    }

    private static final TypeSerializationAdapter<ResponseMessage.Platform> RESPONSE_MESSAGE_PLATFORM =
            new TypeSerializationAdapter<ResponseMessage.Platform>() {
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
            };

    private static final TypeSerializationAdapter<ResponseMessage.MessageType> RESPONSE_MESSAGE_TYPE =
            new TypeSerializationAdapter<ResponseMessage.MessageType>() {
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
            };

    private static final TypeSerializationAdapter<ResponseMessage> RESPONSE_MESSAGE =
            new TypeSerializationAdapter<ResponseMessage>() {
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
            };

    private static final JsonDeserializer<ResponseMessage.ResponseSpeech> RESPONSE_MESSAGE_SPEECH =
            (json, typeOfT, context) -> {

                if (json.isJsonObject() && ((JsonObject) json).get("speech").isJsonPrimitive()) {
                    JsonArray array = new JsonArray();
                    array.add(((JsonObject) json).get("speech"));
                    ((JsonObject) json).add("speech", array);
                }

                return DEFAULT_GSON.fromJson(json, typeOfT);
            };

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
