package com.voidaspect.public24.service.agent.format;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

@Service
public final class CurrencyFormatService implements Function<BigDecimal, String>  {

    @Override
    public String apply(BigDecimal bigDecimal) {
        BigDecimal result = bigDecimal.stripTrailingZeros();
        if (result.scale() < 2) {
            result = result.setScale(2, RoundingMode.UNNECESSARY);
        }
        return result.toPlainString();
    }

}
