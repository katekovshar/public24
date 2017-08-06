package com.voidaspect.public24.service.p24;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExchangeRateType {

    CASH(5),

    NON_CASH(11);

    private final int id;
}
