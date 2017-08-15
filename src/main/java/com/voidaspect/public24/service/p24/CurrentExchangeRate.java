package com.voidaspect.public24.service.p24;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for current exchange rate
 */
@Data
public final class CurrentExchangeRate {

    @SerializedName("ccy")
    private String currency;

    @SerializedName("base_ccy")
    private String baseCurrency;

    @SerializedName("buy")
    private BigDecimal buyRate;

    @SerializedName("sale")
    private BigDecimal saleRate;

}
