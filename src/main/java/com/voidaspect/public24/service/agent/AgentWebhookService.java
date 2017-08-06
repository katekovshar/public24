package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.service.p24.Currency;
import com.voidaspect.public24.service.p24.ExchangeRateHistory;
import com.voidaspect.public24.service.p24.ExchangeRateType;
import com.voidaspect.public24.service.p24.Privat24;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.stream.Collectors;

import static com.voidaspect.public24.service.agent.RequestParams.*;

@Service
@Slf4j
public final class AgentWebhookService implements AgentWebhook { //TODO tests

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
        final String textOutput;
        val currencyCode = incompleteResult.getStringParameter(CURRENCY.getName());
        val currency = Currency.getByName(currencyCode);
        switch (intent) {
            case CURRENT_EXCHANGE_RATE:
                val exchangeRateTypeName = incompleteResult.getStringParameter(EXCHANGE_RATE_TYPE.getName(), ExchangeRateType.NON_CASH.getName());
                val exchangeRateType = ExchangeRateType.getByName(exchangeRateTypeName);
                textOutput = currency
                        .flatMap(ccy -> privat24.getCurrentExchangeRates(exchangeRateType, ccy))
                        .map(e -> "Purchase: " + e.getBuyRate() + ", sale: " + e.getSaleRate())
                        .orElseGet(() -> privat24.getCurrentExchangeRates(exchangeRateType)
                                .stream()
                                .map(e -> "Purchase: " + e.getBuyRate() + ", sale: " + e.getSaleRate())
                                .collect(Collectors.joining("\n")));

                break;
            case EXCHANGE_RATE_HISTORY:
                val localDate = incompleteResult.getDateParameter(DATE.getName())
                        .toInstant()
                        .atZone(ZONE_ID).toLocalDate();
                textOutput = currency
                        .map(ccy -> privat24.getExchangeRatesForDate(localDate, ccy))
                        .map(this::getExchangeRateHistoryText)
                        .orElseGet(() -> getExchangeRateHistoryText(
                                privat24.getExchangeRatesForDate(localDate)));
                break;
            default:
                throw new IllegalStateException("Unreachable statement");
        }

        log.debug("Output speech: {}", textOutput);

        fulfillment.setDisplayText(textOutput);
        fulfillment.setSpeech(textOutput);
        fulfillment.setSource(SOURCE);
        return fulfillment;
    }

    private String getExchangeRateHistoryText(ExchangeRateHistory exchangeRatesForDate) {
        return exchangeRatesForDate
                .getExchangeRates().stream()
                .map(e -> e.getCurrency() + ": " + e.getPurchaseRate() + " - purchase" + e.getSaleRate() + " - sale")
                .collect(Collectors.joining(
                        "\n",
                        "Base currency: " + exchangeRatesForDate.getBaseCurrency(),
                        "Bank: " + exchangeRatesForDate.getBank()
                ));
    }


}
