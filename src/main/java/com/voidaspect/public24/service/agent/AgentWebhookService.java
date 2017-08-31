package com.voidaspect.public24.service.agent;

import ai.api.model.Fulfillment;
import ai.api.model.Result;
import com.voidaspect.public24.controller.AiWebhookRequest;
import com.voidaspect.public24.controller.BadWebhookRequestException;
import com.voidaspect.public24.service.p24.*;
import com.voidaspect.public24.service.p24.Currency;
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

import static com.voidaspect.public24.service.agent.RequestParam.*;

/**
 * {@link AgentWebhook} implementation which relies on:
 * <ul>
 * <li>{@link Privat24}
 * <li>{@link Responses}
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
     * BigDecimal-to-String converter for currency values
     */
    private final Function<BigDecimal, String> currencyFormat;

    /**
     * DI-managed constructor.
     *
     * @param privat24       value of {@link #privat24}
     * @param currencyFormat value of {@link #currencyFormat}
     */
    @Autowired
    public AgentWebhookService(Privat24 privat24, Function<BigDecimal, String> currencyFormat) {
        this.privat24 = privat24;
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

        final Fulfillment fulfillment;
        switch (intent) {
            case CURRENT_EXCHANGE_RATE: {
                val exchangeRateTypeName = incompleteResult.getStringParameter(EXCHANGE_RATE_TYPE.getName(), ExchangeRateType.NON_CASH.getName());
                val exchangeRateType = ExchangeRateType.getByName(exchangeRateTypeName);
                val currency = getStringParamIfPresent(incompleteResult, CURRENCY)
                        .flatMap(Currency::getByName);
                val rates = currency
                        .map(ccy -> privat24.getCurrentExchangeRates(exchangeRateType, ccy)
                                .map(Collections::singletonList)
                                .orElseGet(Collections::emptyList))
                        .orElseGet(() -> privat24.getCurrentExchangeRates(exchangeRateType))
                        .stream()
                        .map(e -> getExchangeRateDescription(
                                e.getCurrency(),
                                e.getBuyRate(),
                                e.getSaleRate()))
                        .collect(Collectors.toList());
                val messageList = SimpleMessageList.builder()
                        .header("Current exchange rate for " + Currency.UAH)
                        .messages(rates)
                        .fallback("No exchange rate found for current date" +
                                currency.map(c -> " and currency " + c + ".")
                                        .orElse("."))
                        .build();
                fulfillment = Responses.fromSimpleStringList(messageList);
                break;
            }
            case EXCHANGE_RATE_HISTORY: {
                val localDate = incompleteResult.getDateParameter(DATE.getName(), new Date())
                        .toInstant().atZone(ZONE_ID).toLocalDate();
                val isoDate = localDate.format(DateTimeFormatter.ISO_DATE);
                val currency = getStringParamIfPresent(incompleteResult, CURRENCY)
                        .flatMap(Currency::getByName);
                log.debug("Retrieving currency exchange history for date {} and {} ccy", isoDate,
                        currency.map(Enum::name).orElse("unspecified"));
                val rates = currency
                        .map(ccy -> privat24.getExchangeRatesForDate(localDate, ccy))
                        .map(ExchangeRateHistory::getExchangeRates)
                        .orElseGet(() -> privat24.getExchangeRatesForDate(localDate).getExchangeRates())
                        .stream()
                        .map(e -> getExchangeRateDescription(
                                e.getCurrency(),
                                Optional.ofNullable(e.getPurchaseRate()).orElseGet(e::getPurchaseRateNB),
                                Optional.ofNullable(e.getSaleRate()).orElseGet(e::getSaleRateNB)))
                        .collect(Collectors.toList());
                val messageList = SimpleMessageList.builder()
                        .header("Exchange rate for " + Currency.UAH + " on " + isoDate)
                        .messages(rates)
                        .fallback("No exchange rate history found for date " + isoDate +
                                currency.map(c -> " and currency " + c + ".")
                                        .orElse("."))
                        .build();
                fulfillment = Responses.fromSimpleStringList(messageList);
                break;
            }
            case INFRASTRUCTURE_LOCATION: {
                val deviceType = getStringParamIfPresent(incompleteResult, INFRASTRUCTURE_TYPE)
                        .flatMap(DeviceType::getByName)
                        .orElseThrow(() -> new BadWebhookRequestException("No supported device type fond in request"));
                val city = getStringParam(incompleteResult, CITY);
                val address = getStringParam(incompleteResult, ADDRESS);
                log.debug("Retrieving infrastructure location data for device type '{}', city '{}', address '{}'", deviceType, city, address);
                Infrastructure infrastructureLocations = privat24.getInfrastructureLocations(deviceType, city, address);
                val messages = infrastructureLocations.getDevices().stream()
                        .map(Device::getFullAddressEn)
                        .collect(Collectors.toList());
                val messageList = SimpleMessageList.builder() //todo google maps
                        .header(deviceType + " locations in " + city + ", " + address)
                        .messages(messages)
                        .fallback("No infrastructure found for given location")
                        .build();
                fulfillment = Responses.fromSimpleStringList(messageList);
                break;
            }
            default:
                throw new UnsupportedOperationException("Intent Not Supported");
        }
        return fulfillment;
    }

    private Optional<String> getStringParamIfPresent(Result data, RequestParam requestParam) {
        return Optional.ofNullable(data.getStringParameter(requestParam.getName(), null));
    }


    private String getStringParam(Result data, RequestParam requestParam) {
        return data.getStringParameter(requestParam.getName());
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
