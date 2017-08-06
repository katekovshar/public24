package com.voidaspect.public24.service.p24;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author mikhail.h
 */
public interface Privat24 {

    ExchangeRateHistory getExchangeRatesForDate(LocalDate date);

    ExchangeRateHistory getExchangeRatesForDate(LocalDate date, Currency currency);

    List<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType);

    Optional<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType, Currency currency);


}
