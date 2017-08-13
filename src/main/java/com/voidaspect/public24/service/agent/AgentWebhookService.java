package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.ResponseMessage;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.service.p24.Currency;
import com.voidaspect.public24.service.p24.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.voidaspect.public24.service.agent.RequestParams.*;

@Service
@Slf4j
public final class AgentWebhookService implements AgentWebhook {

    private static final String SOURCE = "Privat24 API";

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

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
        val currencyCode = incompleteResult.getStringParameter(CURRENCY.getName());
        Optional<Currency> currency = Currency.getByName(currencyCode);
        List<String> messages = new ArrayList<>();
        switch (intent) {
            case CURRENT_EXCHANGE_RATE:
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
                validateExchangeCourseMessageList(messages,
                        "No exchange rate found for current date" +
                                currency.map(c -> " and currency " + c + ".")
                                        .orElse("."));
                break;
            case EXCHANGE_RATE_HISTORY:
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
                validateExchangeCourseMessageList(messages,
                        "No exchange rate history found for date " + isoDate +
                                currency.map(c -> " and currency " + c + ".")
                                        .orElse("."));
                break;
            default:
                throw new IllegalStateException("Unreachable statement");
        }
        List<ResponseMessage> responseSpeechList = messages.stream()
                .map(m -> {
                    val responseSpeech = new ResponseMessage.ResponseSpeech();
                    responseSpeech.setSpeech(m);
                    return responseSpeech;
                })
                .collect(Collectors.toList());
        fulfillment.setMessages(responseSpeechList);
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
        return currencyCode +
                ": purchase = " + formatCurrency(purchase)
                + " sale = " + formatCurrency(sale);
    }


    private static String formatCurrency(BigDecimal value) {
        BigDecimal bigDecimal = value.stripTrailingZeros();
        if (bigDecimal.scale() < 2) {
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_UNNECESSARY);
        }
        return bigDecimal.toPlainString();
    }
}
