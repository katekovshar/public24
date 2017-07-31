package com.voidaspect.public24.service;

import ai.api.util.IOUtils;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * @author mikhail.h
 */
@RunWith(SpringRunner.class)
@RestClientTest(Privat24Service.class)
public class Privat24ServiceTest {

    @Autowired
    private MockRestServiceServer restServiceServer;

    @Autowired
    private DateTimeFormatter formatter;

    @Autowired
    private Privat24Service privat24Service;

    @Autowired
    private Gson gson;

    private String responseBody;

    @Before
    public void setUp() throws Exception {
        URI uri = URI.create("https://api.privatbank.ua/p24api/exchange_rates?json&date=" + LocalDate.now().format(formatter));
        responseBody = IOUtils.readAll(getClass().getResourceAsStream("/data/exchange-rate.json"));
        restServiceServer.expect(requestTo(uri))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetExchangeRateForDate() throws Exception {
        ExchangeRateData exchangeRatesForDate = privat24Service.getExchangeRatesForDate(LocalDate.now());
        assertEquals(gson.fromJson(responseBody, ExchangeRateData.class), exchangeRatesForDate);
    }
}