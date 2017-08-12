package com.voidaspect.public24.service.p24;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author mikhail.h
 */
@RunWith(SpringRunner.class)
@RestClientTest(value = Privat24Service.class, includeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {GsonConfig.class, Privat24Properties.class}))
public class Privat24ServiceTest {

    @Autowired
    private MockRestServiceServer restServiceServer;

    @Autowired
    private DateTimeFormatter formatter;

    @Autowired
    private Privat24Service privat24Service;

    @Autowired
    private Gson gson;

    private String responseBodyExchangeRate;

    private String responseBodyCurrentExchangeRate;

    @Before
    public void setUp() throws Exception {
        restServiceServer.reset();
        responseBodyExchangeRate = IOUtils.readAll(getClass().getResourceAsStream("/data/exchange-rate.json"));
        responseBodyCurrentExchangeRate = IOUtils.readAll(getClass().getResourceAsStream("/data/current-exchange-rate.json"));
    }

    @Test
    public void testGetCurrentRate() throws Exception {
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON));
        List<CurrentExchangeRate> currentExchangeRates = privat24Service.getCurrentExchangeRates(ExchangeRateType.CASH);
        restServiceServer.verify();
        List<CurrentExchangeRate> expected = Arrays.asList(gson.fromJson(responseBodyCurrentExchangeRate, CurrentExchangeRate[].class));
        assertEquals(expected, currentExchangeRates);

        restServiceServer.reset();

        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON));
        List<CurrentExchangeRate> currentExchangeRatesNonCash = privat24Service.getCurrentExchangeRates(ExchangeRateType.NON_CASH);
        restServiceServer.verify();
        List<CurrentExchangeRate> expectedNonCash = Arrays.asList(gson.fromJson(responseBodyCurrentExchangeRate, CurrentExchangeRate[].class));
        assertEquals(expectedNonCash, currentExchangeRatesNonCash);
    }

    @Test
    @DirtiesContext
    public void testGetCurrentRateForCurrency() throws Exception {

        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON));

        String json = "{\n" +
                "    \"ccy\":\"USD\",\n" +
                "    \"base_ccy\":\"UAH\",\n" +
                "    \"buy\":\"15.50000\",\n" +
                "    \"sale\":\"15.85000\"\n" +
                "  }";
        CurrentExchangeRate currentExchangeRate = privat24Service.getCurrentExchangeRates(ExchangeRateType.NON_CASH, Currency.USD)
                .orElseThrow(() -> {
                    fail("Currency not supported");
                    return new RuntimeException("unreachable");
                });
        restServiceServer.verify();
        assertEquals(gson.fromJson(json, CurrentExchangeRate.class), currentExchangeRate);

    }

    @Test
    public void testGetExchangeRateForDate() throws Exception {

        responseBodyExchangeRate = IOUtils.readAll(getClass().getResourceAsStream("/data/exchange-rate.json"));
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/exchange_rates?json&date=" + LocalDate.now().format(formatter))))
                .andRespond(withSuccess(responseBodyExchangeRate, MediaType.APPLICATION_JSON));

        ExchangeRateHistory exchangeRatesForDate = privat24Service.getExchangeRatesForDate(LocalDate.now());
        restServiceServer.verify();
        assertEquals(gson.fromJson(responseBodyExchangeRate, ExchangeRateHistory.class), exchangeRatesForDate);
    }

    @Test
    @DirtiesContext
    public void testGetExchangeRateForDateAndCurrency() throws Exception {


        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/exchange_rates?json&date=" + LocalDate.now().format(formatter))))
                .andRespond(withSuccess(responseBodyExchangeRate, MediaType.APPLICATION_JSON));

        ExchangeRateHistory exchangeRate = privat24Service.getExchangeRatesForDate(LocalDate.now(), Currency.USD);

        restServiceServer.verify();
        String json = "{\n" +
                "  \"date\": \"01.12.2014\",\n" +
                "  \"bank\": \"PB\",\n" +
                "  \"baseCurrency\": 980,\n" +
                "  \"baseCurrencyLit\": \"UAH\",\n" +
                "  \"exchangeRate\": [{\n" +
                "      \"baseCurrency\": \"UAH\"," +
                "      \"currency\": \"USD\"," +
                "      \"saleRateNB\": 15.056413," +
                "      \"purchaseRateNB\": 15.056413," +
                "      \"saleRate\": 15.7," +
                "      \"purchaseRate\": 15.35" +
                "    }]" +
                "}";
        assertEquals(gson.fromJson(json, ExchangeRateHistory.class), exchangeRate);
    }
}