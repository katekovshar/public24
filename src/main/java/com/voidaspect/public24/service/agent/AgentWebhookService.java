package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.service.agent.response.ResponseFactory;
import com.voidaspect.public24.service.p24.Currency;
import com.voidaspect.public24.service.p24.ExchangeRateHistory;
import com.voidaspect.public24.service.p24.ExchangeRateType;
import com.voidaspect.public24.service.p24.Privat24;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voidaspect.public24.service.agent.RequestParams.*;

/**
 * {@link AgentWebhook} implementation which relies on:
 * <ul>
 * <li>{@link Privat24}
 * <li>{@link ResponseFactory}
 * </ul>
 */
@Service
@Slf4j
public final class AgentWebhookService implements AgentWebhook {

    /**
     * Id of a time zone (system default is used)
     */
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * Privat24 API service
     */
    private final Privat24 privat24;

    /**
     * Response data factory
     */
    private final ResponseFactory responseFactory;

    /**
     * BigDecimal-to-String converter for currency values
     */
    private final Function<BigDecimal, String> currencyFormat;

    /**
     * DI-managed constructor.
     *
     * @param privat24        value of {@link #privat24}
     * @param responseFactory value of {@link #responseFactory}
     * @param currencyFormat  value of {@link #currencyFormat}
     */
    @Autowired
    public AgentWebhookService(Privat24 privat24, ResponseFactory responseFactory, Function<BigDecimal, String> currencyFormat) {
        this.privat24 = privat24;
        this.responseFactory = responseFactory;
        this.currencyFormat = currencyFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fulfillment fulfillAgentResponse(AiWebhookRequest aiWebhookRequest) {
        val incompleteResult = aiWebhookRequest.getResult();
        val intentName = incompleteResult.getMetadata().getIntentName();
        val intent = Intent.getByName(intentName);

        val currencyCode = incompleteResult.getStringParameter(CURRENCY.getName());
        Optional<Currency> currency = Currency.getByName(currencyCode);
        List<String> messages = new ArrayList<>();
        final Fulfillment fulfillment;
        switch (intent) {
            case CURRENT_EXCHANGE_RATE: {
                val exchangeRateTypeName = incompleteResult.getStringParameter(EXCHANGE_RATE_TYPE.getName(), ExchangeRateType.NON_CASH.getName());
                val exchangeRateType = ExchangeRateType.getByName(exchangeRateTypeName);
                messages.add("Current exchange rate for " + Currency.UAH);
                messages.addAll(currency
                        .map(ccy -> privat24.getCurrentExchangeRates(exchangeRateType, ccy)
                                .map(Collections::singletonList)
                                .orElseGet(Collections::emptyList))
                        .orElseGet(() -> privat24.getCurrentExchangeRates(exchangeRateType))
                        .stream()
                        .map(e -> getExchangeRateDescription(
                                e.getCurrency(),
                                e.getBuyRate(),
                                e.getSaleRate()))
                        .collect(Collectors.toList()));
                String fallback = "No exchange rate found for current date" +
                        currency.map(c -> " and currency " + c + ".")
                                .orElse(".");
                fulfillment = responseFactory.fromSimpleStringList(messages, fallback);
                break;
            }
            case EXCHANGE_RATE_HISTORY: {
                val localDate = incompleteResult.getDateParameter(DATE.getName(), new Date())
                        .toInstant().atZone(ZONE_ID).toLocalDate();
                val isoDate = localDate.format(DateTimeFormatter.ISO_DATE);
                log.debug("Retrieving currency exchange history for date {} and {} ccy", isoDate,
                        currency.map(Enum::name).orElse("unspecified"));
                messages.add("Exchange rate for " + Currency.UAH + " on " + isoDate);
                messages.addAll(currency
                        .map(ccy -> privat24.getExchangeRatesForDate(localDate, ccy))
                        .map(ExchangeRateHistory::getExchangeRates)
                        .orElseGet(() -> privat24.getExchangeRatesForDate(localDate)
                                .getExchangeRates())
                        .stream()
                        .map(e -> getExchangeRateDescription(
                                e.getCurrency(),
                                Optional.ofNullable(e.getPurchaseRate()).orElseGet(e::getPurchaseRateNB),
                                Optional.ofNullable(e.getSaleRate()).orElseGet(e::getSaleRateNB)))
                        .collect(Collectors.toList()));
                String fallback = "No exchange rate history found for date " + isoDate +
                        currency.map(c -> " and currency " + c + ".")
                                .orElse(".");
                fulfillment = responseFactory.fromSimpleStringList(messages, fallback);
                break;
            }
            default:
                throw new IllegalStateException("Unreachable statement");
        }
        return fulfillment;
    }

    /**
     * Converts exchange rate data to message string
     *
     * @param currencyCode ccy code
     * @param purchase     purchase value
     * @param sale         sale value
     * @return response message
     */
    private String getExchangeRateDescription(String currencyCode, BigDecimal purchase, BigDecimal sale) {
        return currencyCode +
                ": purchase = " + currencyFormat.apply(purchase)
                + " sale = " + currencyFormat.apply(sale);
    }


}
