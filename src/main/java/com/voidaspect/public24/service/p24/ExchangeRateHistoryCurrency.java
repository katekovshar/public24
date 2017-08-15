package com.voidaspect.public24.service.p24;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for exchange rate history for specific currency
 * @author mikhail.h
 */
@Data
public final class ExchangeRateHistoryCurrency {

    private String baseCurrency;

    private String currency;

    private BigDecimal saleRateNB;

    private BigDecimal saleRate;

    private BigDecimal purchaseRateNB;

    private BigDecimal purchaseRate;

}
