package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.Metadata;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.voidaspect.public24.service.agent.format.CurrencyFormatService;
import com.voidaspect.public24.config.gson.GsonConfig;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.controller.BadWebhookRequestException;
import com.voidaspect.public24.service.agent.response.ResponseService;
import com.voidaspect.public24.service.p24.*;
import com.voidaspect.public24.service.p24.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {AgentWebhookService.class, ResponseService.class, GsonConfig.class, CurrencyFormatService.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AgentWebhookServiceTest {

    private static final String SOURCE = "Privat24 API";

    private static final int BASE_CURRENCY = 980;

    private static final String BASE_CURRENCY_LIT = Currency.UAH.name();

    @MockBean
    private Privat24 privat24;

    @Autowired
    private AgentWebhookService agentWebhookService;

    @Autowired
    private Function<BigDecimal, String> currencyFormat;

    @Autowired
    private Gson gson;

    @Test
    public void testUnknownIntent() throws Exception {
        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest("unknown");
        try {
            agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
            fail("Exception expected");
        } catch (BadWebhookRequestException e) {
            assertEquals("Unable to resolve intent name: unknown", e.getMessage());
        }
        verifyZeroInteractions(privat24);
    }


    @Test
    public void testExchangeRateHistory_CAD() throws Exception {
        Intent intent = Intent.EXCHANGE_RATE_HISTORY;
        Currency currency = Currency.CAD;
        LocalDate date = LocalDate.now().minusMonths(2);
        ExchangeRateHistory exchangeRateHistory = createExchangeRateHistory(date, currency);

        given(privat24.getExchangeRatesForDate(date, currency))
                .willReturn(exchangeRateHistory);

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        HashMap<String, JsonElement> parameters = aiWebhookRequest.getResult().getParameters();
        parameters.put(RequestParams.DATE.getName(), gson.toJsonTree(localDateToDate(date)));
        parameters.put(RequestParams.CURRENCY.getName(), gson.toJsonTree(currency));

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, only())
                .getExchangeRatesForDate(date, currency);
        assertFulfillment(fulfillment, exchangeRateHistory);
    }

    private static Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testExchangeRateHistory() throws Exception {
        Intent intent = Intent.EXCHANGE_RATE_HISTORY;
        LocalDate date = LocalDate.now().minusMonths(1);
        ExchangeRateHistory exchangeRateHistory = createExchangeRateHistory(date);

        given(privat24.getExchangeRatesForDate(date))
                .willReturn(exchangeRateHistory);

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        aiWebhookRequest.getResult().getParameters()
                .put(RequestParams.DATE.getName(), gson.toJsonTree(localDateToDate(date)));

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, only())
                .getExchangeRatesForDate(date);
        assertFulfillment(fulfillment, exchangeRateHistory);
    }

    @Test
    public void testExchangeHistory_notFound() throws Exception {
        Intent intent = Intent.EXCHANGE_RATE_HISTORY;
        Currency currency = Currency.BTC;
        LocalDate date = LocalDate.now().minusMonths(1);
        ExchangeRateHistory exchangeRateHistory = createEmptyExchangeRateHistory(date);
        given(privat24.getExchangeRatesForDate(date, currency))
                .willReturn(exchangeRateHistory);
        given(privat24.getExchangeRatesForDate(date))
                .willReturn(exchangeRateHistory);

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        HashMap<String, JsonElement> parameters = aiWebhookRequest.getResult().getParameters();
        parameters.put(RequestParams.CURRENCY.getName(), gson.toJsonTree(currency.name()));
        parameters.put(RequestParams.DATE.getName(), gson.toJsonTree(localDateToDate(date)));

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, times(1))
                .getExchangeRatesForDate(date, currency);
        String isoDate = date.format(DateTimeFormatter.ISO_DATE);
        assertFallback(fulfillment,
                "No exchange rate history found for date " + isoDate + " and currency " + currency + ".");

        AiWebhookRequest aiWebhookRequestAllCcy = createAiWebhookRequest(intent.getName());
        aiWebhookRequestAllCcy.getResult().getParameters()
                .put(RequestParams.DATE.getName(), gson.toJsonTree(localDateToDate(date)));
        Fulfillment fulfillmentAllCcy = agentWebhookService.fulfillAgentResponse(aiWebhookRequestAllCcy);
        verify(privat24, times(1))
                .getExchangeRatesForDate(date);
        assertFallback(fulfillmentAllCcy,
                "No exchange rate history found for date " + isoDate + ".");

        verifyNoMoreInteractions(privat24);
    }

    @Test
    public void testCurrentExchange_notFound() throws Exception {
        Intent intent = Intent.CURRENT_EXCHANGE_RATE;
        ExchangeRateType exchangeRateType = ExchangeRateType.NON_CASH;
        Currency currency = Currency.PLZ;
        given(privat24.getCurrentExchangeRates(exchangeRateType, currency))
                .willReturn(Optional.empty());

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        HashMap<String, JsonElement> parameters = aiWebhookRequest.getResult().getParameters();
        parameters.put(RequestParams.CURRENCY.getName(), gson.toJsonTree(currency.name()));
        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, times(1))
                .getCurrentExchangeRates(exchangeRateType, currency);
        assertFallback(fulfillment,
                "No exchange rate found for current date and currency " + currency + ".");

        given(privat24.getCurrentExchangeRates(exchangeRateType))
                .willReturn(Collections.emptyList());
        AiWebhookRequest aiWebhookRequestAllCcy = createAiWebhookRequest(intent.getName());
        Fulfillment fulfillmentAllCcy = agentWebhookService.fulfillAgentResponse(aiWebhookRequestAllCcy);
        verify(privat24, times(1))
                .getCurrentExchangeRates(exchangeRateType);
        assertFallback(fulfillmentAllCcy,
                "No exchange rate found for current date.");

        verifyNoMoreInteractions(privat24);
    }

    private void assertFallback(Fulfillment fulfillment, String fallback) {
        assertFulfillmentContent(fulfillment, Collections.singletonList(
                fallback));
    }

    @Test
    public void testCurrentExchange_nonCash() throws Exception {
        Intent intent = Intent.CURRENT_EXCHANGE_RATE;
        List<CurrentExchangeRate> currentRates = createCurrentRates();
        ExchangeRateType exchangeRateType = ExchangeRateType.NON_CASH;
        given(privat24.getCurrentExchangeRates(exchangeRateType))
                .willReturn(currentRates);

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        aiWebhookRequest.getResult().getParameters().put(RequestParams.EXCHANGE_RATE_TYPE.getName(), gson.toJsonTree(exchangeRateType.getName()));

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, only())
                .getCurrentExchangeRates(exchangeRateType);

        assertFulfillment(fulfillment, currentRates);
    }

    @Test
    public void testCurrentExchange_nonCash_default() throws Exception {
        Intent intent = Intent.CURRENT_EXCHANGE_RATE;
        List<CurrentExchangeRate> currentRates = createCurrentRates();
        ExchangeRateType exchangeRateType = ExchangeRateType.NON_CASH;
        given(privat24.getCurrentExchangeRates(exchangeRateType))
                .willReturn(currentRates);

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, only())
                .getCurrentExchangeRates(exchangeRateType);

        assertFulfillment(fulfillment, currentRates);
    }

    @Test
    public void testCurrentExchange_USD_cash() throws Exception {
        Intent intent = Intent.CURRENT_EXCHANGE_RATE;
        ExchangeRateType exchangeRateType = ExchangeRateType.CASH;
        Currency currency = Currency.USD;
        CurrentExchangeRate currentRate = createCurrentRate(currency);
        given(privat24.getCurrentExchangeRates(exchangeRateType, currency))
                .willReturn(Optional.of(currentRate));

        AiWebhookRequest aiWebhookRequest = createAiWebhookRequest(intent.getName());
        HashMap<String, JsonElement> parameters = aiWebhookRequest.getResult().getParameters();
        parameters.put(RequestParams.EXCHANGE_RATE_TYPE.getName(), gson.toJsonTree(exchangeRateType.getName()));
        parameters.put(RequestParams.CURRENCY.getName(), gson.toJsonTree(currency.name()));

        Fulfillment fulfillment = agentWebhookService.fulfillAgentResponse(aiWebhookRequest);
        verify(privat24, only())
                .getCurrentExchangeRates(exchangeRateType, currency);
        assertFulfillment(fulfillment, currentRate);
    }

    private void assertFulfillment(Fulfillment fulfillment, ExchangeRateHistory exchangeRateHistory) {
        List<String> exchangeRateForUah = new ArrayList<>();
        exchangeRateForUah.add("Exchange rate for " + Currency.UAH + " on " + exchangeRateHistory.getDate().format(DateTimeFormatter.ISO_DATE));
        for (ExchangeRateHistoryCurrency rate : exchangeRateHistory.getExchangeRates()) {
            exchangeRateForUah.add(rate.getCurrency() + ": purchase = " + currencyFormat.apply(rate.getPurchaseRate()) +
                    " sale = " + currencyFormat.apply(rate.getSaleRate()));
        }
        assertFulfillmentContent(fulfillment, exchangeRateForUah);
    }

    private void assertFulfillment(Fulfillment fulfillment, CurrentExchangeRate... currentRates) {
        assertFulfillment(fulfillment, Arrays.asList(currentRates));
    }

    private void assertFulfillment(Fulfillment fulfillment, Collection<CurrentExchangeRate> currentRates) {
        List<String> exchangeRateForUah = new ArrayList<>();
        exchangeRateForUah.add("Current exchange rate for UAH");
        for (CurrentExchangeRate currentRate : currentRates) {
            exchangeRateForUah.add(currentRate.getCurrency() + ": purchase = " + currencyFormat.apply(currentRate.getBuyRate()) +
                    " sale = " + currencyFormat.apply(currentRate.getSaleRate()));
        }
        assertFulfillmentContent(fulfillment, exchangeRateForUah);
    }

    private void assertFulfillmentContent(Fulfillment fulfillment, List<String> messages) {
        List<String> responseSpeech = fulfillment.getMessages()
                .stream()
                .map(m -> ((ResponseMessage.ResponseSpeech) m).getSpeech().get(0))
                .collect(Collectors.toList());
        assertEquals(messages, responseSpeech);
        assertEquals(
                messages.stream().collect(Collectors.joining("\n")),
                fulfillment.getSpeech());
        assertEquals(SOURCE, fulfillment.getSource());
    }

    private AiWebhookRequest createAiWebhookRequest(String intent) throws Exception {
        AiWebhookRequest aiWebhookRequest = new AiWebhookRequest();
        Result partialResult = new Result();
        partialResult.setActionIncomplete(true);
        Field parameters = Result.class.getDeclaredField("parameters");
        parameters.setAccessible(true);
        parameters.set(partialResult, new HashMap<String, JsonElement>());
        parameters.setAccessible(false);
        Metadata metadata = new Metadata();
        metadata.setWebhookUsed(true);
        metadata.setIntentName(intent);
        partialResult.setMetadata(metadata);
        aiWebhookRequest.setResult(partialResult);
        return aiWebhookRequest;
    }

    private ExchangeRateHistory createExchangeRateHistory(LocalDate date) {
        return createExchangeRateHistory(date, Currency.values());
    }

    private ExchangeRateHistory createExchangeRateHistory(LocalDate date, Currency... currencies) {
        ExchangeRateHistory exchangeRateHistory = createEmptyExchangeRateHistory(date);
        List<ExchangeRateHistoryCurrency> exchangeRates = Arrays.stream(currencies)
                .map(this::createExchangeRateHistoryCurrency)
                .collect(Collectors.toList());
        exchangeRateHistory.setExchangeRates(exchangeRates);
        return exchangeRateHistory;
    }

    private ExchangeRateHistory createEmptyExchangeRateHistory(LocalDate date) {
        ExchangeRateHistory exchangeRateHistory = new ExchangeRateHistory();
        exchangeRateHistory.setBaseCurrencyLit(BASE_CURRENCY_LIT);
        exchangeRateHistory.setBaseCurrency(BASE_CURRENCY);
        exchangeRateHistory.setBank("PB");
        exchangeRateHistory.setDate(date);
        exchangeRateHistory.setExchangeRates(Collections.emptyList());
        return exchangeRateHistory;
    }

    private ExchangeRateHistoryCurrency createExchangeRateHistoryCurrency(Currency currency) {
        ExchangeRateHistoryCurrency item = new ExchangeRateHistoryCurrency();
        item.setBaseCurrency(BASE_CURRENCY_LIT);
        item.setCurrency(currency.name());
        Random random = new Random();
        double positiveDouble = random.nextDouble() * random.nextInt(10000);
        BigDecimal purchaseRate = new BigDecimal(positiveDouble).setScale(10, RoundingMode.HALF_UP);
        item.setPurchaseRate(purchaseRate);
        BigDecimal saleRate = new BigDecimal(positiveDouble + random.nextInt(2));
        item.setSaleRate(saleRate);
        item.setSaleRateNB(saleRate.subtract(BigDecimal.ONE).abs());
        item.setPurchaseRateNB(purchaseRate.add(BigDecimal.ONE));
        return item;
    }

    private List<CurrentExchangeRate> createCurrentRates() {
        return Stream.of(Currency.USD, Currency.RUR, Currency.BTC, Currency.EUR)
                .map(this::createCurrentRate)
                .collect(Collectors.toList());
    }

    private CurrentExchangeRate createCurrentRate(Currency currency) {
        CurrentExchangeRate privat24Response = new CurrentExchangeRate();
        privat24Response.setBaseCurrency(BASE_CURRENCY_LIT);
        privat24Response.setCurrency(currency.name());
        Random random = new Random();
        double positiveDouble = random.nextDouble() * random.nextInt(10000);
        privat24Response.setBuyRate(new BigDecimal(positiveDouble).setScale(10, RoundingMode.HALF_UP));
        privat24Response.setSaleRate(new BigDecimal(positiveDouble + random.nextInt(2)));
        return privat24Response;
    }
}