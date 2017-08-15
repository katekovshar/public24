package com.voidaspect.public24.service.agent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Parameters used in webhook request
 */
@RequiredArgsConstructor
@Getter
enum RequestParams {

    /**
     * Date - should be {@link java.util.Date}
     */
    DATE("date"),

    /**
     * Currency code should be {@link String}
     */
    CURRENCY("ccy"),

    /**
     * Exchange rate type - cash or non-cash
     */
    EXCHANGE_RATE_TYPE("exchange-rate-type");

    /**
     * Name of a parameter used in API.AI
     */
    private final String name;
}
