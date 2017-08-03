package com.voidaspect.public24.service;

import ai.api.util.IOUtils;
import com.google.gson.Gson;
import com.voidaspect.public24.config.gson.GsonConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author mikhail.h
 */
@RunWith(SpringRunner.class)
@RestClientTest(value = Privat24Service.class, includeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GsonConfig.class))
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

    @Test
    public void testGetExchangeRateForDateAndCurrency() throws Exception {
        ExchangeRate exchangeRate = privat24Service.getExchangeRatesForDate(LocalDate.now(), Currency.USD)
                .orElseThrow(() -> {
                    fail("Currency not supported");
                    return new RuntimeException("unreachable");
                });
        String json = "{\n" +
                "      \"baseCurrency\": \"UAH\"," +
                "      \"currency\": \"USD\"," +
                "      \"saleRateNB\": 15.056413," +
                "      \"purchaseRateNB\": 15.056413," +
                "      \"saleRate\": 15.7," +
                "      \"purchaseRate\": 15.35" +
                "    }";
        assertEquals(gson.fromJson(json, ExchangeRate.class), exchangeRate);
    }
}