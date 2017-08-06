package com.voidaspect.public24.service.p24;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author mikhail.h
 */
public enum Currency {

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

    public static Optional<Currency> getByName(String currencyCode) {
        return Arrays.stream(values())
                .filter(currency -> currency.name().equalsIgnoreCase(currencyCode))
                .findAny();
    }
}
