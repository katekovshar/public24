package com.voidaspect.public24.service;

import java.time.LocalDate;

/**
 * @author mikhail.h
 */
public interface Privat24 {

    ExchangeRateData getExchangeRatesForDate(LocalDate date);

}
