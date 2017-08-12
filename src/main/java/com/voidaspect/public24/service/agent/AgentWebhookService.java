package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.service.p24.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voidaspect.public24.service.agent.RequestParams.*;

@Service
@Slf4j
public final class AgentWebhookService implements AgentWebhook {

    private static final String SOURCE = "Privat24 API";

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private static final Function<CurrentExchangeRate, String> CURRENT_EXCHANGE_RATE_STRING_FUNCTION =
            e -> getExchangeRateDescription(
                    Currency.valueOf(e.getCurrency()),
                    e.getBuyRate(),
                    e.getSaleRate());

    private static final Function<ExchangeRateHistoryCurrency, String> EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION =
            e -> getExchangeRateDescription(
                    Currency.valueOf(e.getCurrency()),
                    e.getPurchaseRate(),
                    e.getSaleRate());

    private final Privat24 privat24;

    public AgentWebhookService(Privat24 privat24) {
        this.privat24 = privat24;
    }

    @Override
    public Fulfillment fulfillAgentResponse(AiWebhookRequest aiWebhookRequest) {
        val incompleteResult = aiWebhookRequest.getResult();
        val intentName = incompleteResult.getMetadata().getIntentName();
        val intent = Intent.getByName(intentName);

        val fulfillment = new Fulfillment();
//        final String textOutput;
        val currencyCode = incompleteResult.getStringParameter(CURRENCY.getName());
        val currency = Currency.getByName(currencyCode);
        val responseSpeech = new ResponseMessage.ResponseSpeech();
        List<String> messages = new ArrayList<>();
        switch (intent) {
            case CURRENT_EXCHANGE_RATE:
                val exchangeRateTypeName = incompleteResult.getStringParameter(EXCHANGE_RATE_TYPE.getName(), ExchangeRateType.NON_CASH.getName());
                val exchangeRateType = ExchangeRateType.getByName(exchangeRateTypeName);
                messages.add("Current exchange rate for " + Currency.UAH);
                messages.addAll(currency
                        .flatMap(ccy -> privat24.getCurrentExchangeRates(exchangeRateType, ccy))
                        .map(CURRENT_EXCHANGE_RATE_STRING_FUNCTION)
                        .map(Collections::singletonList)
                        .orElseGet(() -> privat24.getCurrentExchangeRates(exchangeRateType)
                                .stream()
                                .map(CURRENT_EXCHANGE_RATE_STRING_FUNCTION)
                                .collect(Collectors.toList())));
                responseSpeech.setSpeech(messages);
                fulfillment.setMessages(responseSpeech);

                break;
            case EXCHANGE_RATE_HISTORY:
                val localDate = incompleteResult.getDateParameter(DATE.getName())
                        .toInstant()
                        .atZone(ZONE_ID).toLocalDate();
                messages.add("Exchange rate for " + Currency.UAH + " on " + localDate.format(DateTimeFormatter.ISO_DATE));
                messages.addAll(currency
                        .map(ccy -> privat24.getExchangeRatesForDate(localDate, ccy))
                        .flatMap(e -> e.getExchangeRates().stream().findAny())
                        .map(EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION)
                        .map(Collections::singletonList)
                        .orElseGet(() -> privat24.getExchangeRatesForDate(localDate)
                                .getExchangeRates().stream()
                                .map(EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION)
                                .collect(Collectors.toList())));
                fulfillment.setMessages(responseSpeech);
                break;
            default:
                throw new IllegalStateException("Unreachable statement");
        }

//        log.debug("Output speech: {}", textOutput);

//        fulfillment.setDisplayText(textOutput);
//        fulfillment.setSpeech(textOutput);
//        fulfillment.setMessages(responseMessages);
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

    private static String getExchangeRateDescription(Currency currency, BigDecimal purchase, BigDecimal sale) {
        return currency.name() + ": purchase = " + purchase.toPlainString() + " sale = " + sale.toPlainString();
    }


}
