package com.voidaspect.public24.service.p24;

import java.util.Arrays;
import java.util.Optional;

/**
 * List of currency codes supported for requests
 * @author mikhail.h
 */
public enum Currency {

    UAH,

    AUD,

    USD,

    EUR,

    RUR,

    CHF,

    GBP,
    
    CZK,

    PLZ,

    SEK,

    XAU,

    CAD,

    BTC;

    /**
     * Retrieves a constant by it's name
     * @param currencyCode string value
     * @return optional-wrapped {@link Currency} constant
     */
    public static Optional<Currency> getByName(String currencyCode) {
        return Arrays.stream(values())
                .filter(currency -> currency.name().equalsIgnoreCase(currencyCode))
                .findAny();
    }
}
