package com.voidaspect.public24.service.p24;

import com.google.gson.Gson;
import com.voidaspect.public24.Tests;
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
import static org.junit.Assert.assertSame;
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

    private final String responseBodyExchangeRate =
            Tests.loadTestResourceAsString("/data/p24/exchange-rate.json");

    private final String responseBodyCurrentExchangeRate =
            Tests.loadTestResourceAsString("/data/p24/current-exchange-rate.json");

    @Before
    public void setUp() throws Exception {
        restServiceServer.reset();
    }

    @Test
    public void testGetCurrentRate() throws Exception {
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON_UTF8));
        List<CurrentExchangeRate> currentExchangeRates = privat24Service.getCurrentExchangeRates(ExchangeRateType.CASH);
        restServiceServer.verify();
        List<CurrentExchangeRate> expected = Arrays.asList(gson.fromJson(responseBodyCurrentExchangeRate, CurrentExchangeRate[].class));
        assertEquals(expected, currentExchangeRates);

        restServiceServer.reset();

        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON_UTF8));
        List<CurrentExchangeRate> currentExchangeRatesNonCash = privat24Service.getCurrentExchangeRates(ExchangeRateType.NON_CASH);
        restServiceServer.verify();
        List<CurrentExchangeRate> expectedNonCash = Arrays.asList(gson.fromJson(responseBodyCurrentExchangeRate, CurrentExchangeRate[].class));
        assertEquals(expectedNonCash, currentExchangeRatesNonCash);
    }

    @Test
    @DirtiesContext
    public void testGetCurrentRateForCurrency() throws Exception {
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11")))
                .andRespond(withSuccess(responseBodyCurrentExchangeRate, MediaType.APPLICATION_JSON_UTF8));

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
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/exchange_rates?json&date=" + LocalDate.now().format(formatter))))
                .andRespond(withSuccess(responseBodyExchangeRate, MediaType.APPLICATION_JSON_UTF8));

        ExchangeRateHistory exchangeRatesForDate = privat24Service.getExchangeRatesForDate(LocalDate.now());
        restServiceServer.verify();
        assertEquals(gson.fromJson(responseBodyExchangeRate, ExchangeRateHistory.class), exchangeRatesForDate);
    }

    @Test
    @DirtiesContext
    public void testGetExchangeRateForDateAndCurrency() throws Exception {
        restServiceServer.expect(requestTo(URI.create("https://api.privatbank.ua/p24api/exchange_rates?json&date=" + LocalDate.now().format(formatter))))
                .andRespond(withSuccess(responseBodyExchangeRate, MediaType.APPLICATION_JSON_UTF8));

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

    @Test
    public void testGetInfrastructureLocation_atm_Kharkiv() throws Exception {
        String responseBody = Tests.loadTestResourceAsString("/data/p24/infrastructure-atm-kharkiv.json");

        restServiceServer.expect(requestTo("https://api.privatbank.ua/p24api/infrastructure?json&atm&city=Kharkiv&address="))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON_UTF8));

        Infrastructure infrastructureLocations = privat24Service.getInfrastructureLocations(DeviceType.ATM, "Kharkiv");

        restServiceServer.verify();

        assertEquals("Kharkiv", infrastructureLocations.getCity());
        assertEquals("", infrastructureLocations.getAddress());

        infrastructureLocations.getDevices()
                .forEach(device -> assertSame(DeviceType.ATM, device.getDeviceType()));

    }

    @Test
    public void testGetInfrastructureLocation_tso_Kharkiv_Moskovskiy() throws Exception {
        String responseBody = Tests.loadTestResourceAsString("/data/p24/infrastructure-tso-kharkiv-msk.json");

        restServiceServer.expect(requestTo("https://api.privatbank.ua/p24api/infrastructure?json&tso&city=Kharkiv&address=Moskovskyi"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON_UTF8));

        Infrastructure infrastructureLocations = privat24Service.getInfrastructureLocations(DeviceType.TSO, "Kharkiv", "Moskovskyi");

        restServiceServer.verify();

        assertEquals("Kharkiv", infrastructureLocations.getCity());
        assertEquals("Moskovskyi", infrastructureLocations.getAddress());

        infrastructureLocations.getDevices()
                .forEach(device -> assertSame(DeviceType.TSO, device.getDeviceType()));

    }


}