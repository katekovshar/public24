package com.voidaspect.public24.service.p24;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * List of exchange rate types supported for request
 */
@RequiredArgsConstructor
@Getter
public enum ExchangeRateType {

    CASH(5, "cash"),

    NON_CASH(11, "non-cash");

    /**
     * Id of the type used in request to Privat24 API
     */
    private final int id;

    /**
     * String name used in API.AI entity
     */
    private final String name;

    /**
     * Retrieves a type by his name
     * @param name string value
     * @return {@link ExchangeRateType} constant
     */
    public static ExchangeRateType getByName(String name) {
        return Arrays.stream(values())
                .filter(exchangeRateType -> exchangeRateType.name.equalsIgnoreCase(name))
                .findAny()
                .orElse(NON_CASH);
    }
}
