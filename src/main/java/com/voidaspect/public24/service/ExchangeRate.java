package com.voidaspect.public24.service;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mikhail.h
 */
@Data
public final class ExchangeRate {

    private Currency baseCurrency;

    private Currency currency;

    private BigDecimal saleRateNB;

    private BigDecimal saleRate;

    private BigDecimal purchaseRateNB;

    private BigDecimal purchaseRate;

}
