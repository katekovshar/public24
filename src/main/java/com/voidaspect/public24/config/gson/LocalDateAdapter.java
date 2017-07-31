package com.voidaspect.public24.config.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author mikhail.h
 */
public final class LocalDateAdapter implements
        JsonDeserializer<LocalDate>,
        JsonSerializer<LocalDate> {

    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateAdapter(DateTimeFormatter dateTimeFormatter) {
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
