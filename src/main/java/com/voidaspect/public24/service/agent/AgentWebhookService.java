package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.service.p24.*;
import com.voidaspect.public24.service.p24.Currency;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
                    e.getCurrency(),
                    e.getBuyRate(),
                    e.getSaleRate());

    private static final Function<ExchangeRateHistoryCurrency, String> EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION =
            e -> getExchangeRateDescription(
                    e.getCurrency(),
                    Optional.ofNullable(e.getPurchaseRate()).orElseGet(e::getPurchaseRateNB),
                    Optional.ofNullable(e.getSaleRate()).orElseGet(e::getSaleRateNB));

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
                validateExchangeCourseMessageList(messages,
                        "No exchange course found for current date " +
                                currency.map(c -> "and currency " + c + ".")
                                        .orElse("."));
                break;
            case EXCHANGE_RATE_HISTORY:
                val localDate = Optional.ofNullable(incompleteResult.getDateParameter(DATE.getName()))
                        .orElseGet(Date::new)
                        .toInstant()
                        .atZone(ZONE_ID).toLocalDate();
                val isoDate = localDate.format(DateTimeFormatter.ISO_DATE);
                log.debug("Retrieving currency exchange history for date {}", isoDate);
                messages.add("Exchange rate for " + Currency.UAH + " on " + isoDate);
                messages.addAll(currency
                        .map(ccy -> privat24.getExchangeRatesForDate(localDate, ccy))
                        .flatMap(e -> e.getExchangeRates().stream().findAny())
                        .map(EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION)
                        .map(Collections::singletonList)
                        .orElseGet(() -> privat24.getExchangeRatesForDate(localDate)
                                .getExchangeRates().stream()
                                .map(EXCHANGE_RATE_HISTORY_CURRENCY_STRING_FUNCTION)
                                .collect(Collectors.toList())));
                validateExchangeCourseMessageList(messages,
                        "No exchange course history found for date " + isoDate +
                                currency.map(c -> "and currency " + c + ".")
                                        .orElse("."));
                break;
            default:
                throw new IllegalStateException("Unreachable statement");
        }
        val responseSpeech = new ResponseMessage.ResponseSpeech();
        responseSpeech.setSpeech(messages);
        fulfillment.setMessages(responseSpeech);

//        log.debug("Output speech: {}", textOutput);

//        fulfillment.setDisplayText(textOutput);
//        fulfillment.setSpeech(textOutput);
//        fulfillment.setMessages(responseMessages);
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

    private void validateExchangeCourseMessageList(List<String> messages, String message) {
        if (messages.size() <= 1) {
            messages.clear();
            messages.add(message);
        }
    }

    private static String getExchangeRateDescription(String currencyCode, BigDecimal purchase, BigDecimal sale) {
        return currencyCode + ": purchase = " + purchase.toPlainString() + " sale = " + sale.toPlainString();
    }


}
