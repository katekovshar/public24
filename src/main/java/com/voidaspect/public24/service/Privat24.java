package com.voidaspect.public24.service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author mikhail.h
 */
public interface Privat24 {

    ExchangeRateData getExchangeRatesForDate(LocalDate date);

    Optional<ExchangeRate> getExchangeRatesForDate(LocalDate date, Currency currency);

}
