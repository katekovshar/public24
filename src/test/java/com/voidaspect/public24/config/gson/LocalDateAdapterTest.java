package com.voidaspect.public24.config.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.voidaspect.public24.config.gson.LocalDateAdapter;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.*;

/**
 * @author mikhail.h
 */
public class LocalDateAdapterTest {

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Test
    public void mustSerializeInFormat_ddmmyyyy() throws Exception {
        LocalDate date = LocalDate.of(2017, Month.JULY, 31);
        JsonElement serialized = gson.toJsonTree(date);
        assertEquals("31.07.2017", serialized.getAsString());
        LocalDate deserialized = gson.fromJson(serialized, LocalDate.class);
        assertEquals(date, deserialized);
    }

}