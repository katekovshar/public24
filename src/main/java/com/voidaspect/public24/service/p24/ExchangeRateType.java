package com.voidaspect.public24.service.p24;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ExchangeRateType {

    CASH(5, "cash"),

    NON_CASH(11, "non-cash");

    private final int id;

    private final String name;

    public static ExchangeRateType getByName(String name) {
        return Arrays.stream(values())
                .filter(exchangeRateType -> exchangeRateType.name.equalsIgnoreCase(name))
                .findAny()
                .orElse(NON_CASH);
    }
}
