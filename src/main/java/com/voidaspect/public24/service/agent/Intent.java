package com.voidaspect.public24.service.agent;

import com.voidaspect.public24.controller.BadWebhookRequestException;
import com.voidaspect.public24.service.p24.DeviceType;
import com.voidaspect.public24.service.p24.ExchangeRateType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Describes user intents supported by the agent
 */
@RequiredArgsConstructor
@Getter
enum Intent {

    /**
     * @see com.voidaspect.public24.service.p24.Privat24#getInfrastructureLocations(DeviceType, String, String)
     */
    INFRASTRUCTURE_LOCATION("Infrastructure Location"),

    /**
     * @see com.voidaspect.public24.service.p24.Privat24#getCurrentExchangeRates(ExchangeRateType)
     */
    CURRENT_EXCHANGE_RATE("Current Exchange Rate"),

    /**
     * @see com.voidaspect.public24.service.p24.Privat24Service#getExchangeRatesForDate(LocalDate)
     */
    EXCHANGE_RATE_HISTORY("Exchange Rate History");

    /**
     * name of intent used in API.AI
     */
    private final String name;

    /**
     * Retrieves intent constant by name (spaces and case are ignored)
     * @param name string name of intent
     * @return intent constant
     * @throws BadWebhookRequestException if intent is not found
     */
    static Intent getByName(String name) throws BadWebhookRequestException {
        return Arrays.stream(values())
                .filter(intent -> compareIgnoreCaseAndSpaces(intent.name, name))
                .findAny()
                .orElseThrow(() -> new BadWebhookRequestException(
                        "Unable to resolve intent name: " + name));
    }

    /**
     * Compares two strings ignoring spaces and case
     * @param s1 first string
     * @param s2 second string
     * @return result of comparision
     */
    static boolean compareIgnoreCaseAndSpaces(String s1, String s2) {
        return s1.replace(" ", "")
                .equalsIgnoreCase(s2.replace(" ", ""));
    }
}
