package com.voidaspect.public24.service.p24;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author mikhail.h
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ExchangeRateHistory {

    private LocalDate date;

    private String bank;

    private Integer baseCurrency;

    private String baseCurrencyLit;

    @SerializedName("exchangeRate")
    private List<ExchangeRateHistoryCurrency> exchangeRates;

}
