package com.voidaspect.public24.service.p24;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author mikhail.h
 */
@Data
public final class ExchangeRateData {

    private LocalDate date;

    private String bank;

    private Integer baseCurrency;

    private String baseCurrencyLit;

    @SerializedName("exchangeRate")
    private List<ExchangeRate> exchangeRates;

}
