package com.voidaspect.public24.config.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * @author mikhail.h
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GsonConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AdaptersTest {

    @Autowired
    private Gson gson;

    @Test
    public void mustSerializeInFormat_ddmmyyyy() throws Exception {
        LocalDate date = LocalDate.of(2017, Month.JULY, 31);
        JsonElement serialized = gson.toJsonTree(date);
        assertEquals("31.07.2017", serialized.getAsString());
        LocalDate deserialized = gson.fromJson(serialized, LocalDate.class);
        assertEquals(date, deserialized);
    }

}