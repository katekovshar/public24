package com.voidaspect.public24.service.p24;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for exchange rate history
 * @author mikhail.h
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExchangeRateHistory {

    /**
     * Date used in request for Privat24 API
     */
    private LocalDate date;

    /**
     * Name of bank in a {@link String} format
     */
    private String bank;

    /**
     * Code of base currency in an {@link Integer} format.
     */
    private Integer baseCurrency;

    /**
     * Code of base currency in a {@link String} format.
     */
    private String baseCurrencyLit;


    @SerializedName("exchangeRate")
    private List<ExchangeRateHistoryCurrency> exchangeRates;

}
