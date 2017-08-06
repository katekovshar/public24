package com.voidaspect.public24.service.agent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum RequestParams {

    DATE("date"),

    CURRENCY("ccy"),

    EXCHANGE_RATE_TYPE("exchange-rate-type");

    private final String name;
}
