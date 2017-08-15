package com.voidaspect.public24.service.agent.format;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * Functional service which specifies how currency values are displayed as strings
 */
@Service
public final class CurrencyFormatService implements Function<BigDecimal, String>  {

    /**
     * Strips zeros and sets scale to a minimum of 2
     * @return {@link BigDecimal#toPlainString()}
     */
    @Override
    public String apply(BigDecimal bigDecimal) {
        BigDecimal result = bigDecimal.stripTrailingZeros();
        if (result.scale() < 2) {
            result = result.setScale(2, RoundingMode.UNNECESSARY);
        }
        return result.toPlainString();
    }

}
