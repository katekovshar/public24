package com.voidaspect.public24.service.p24;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for Privat24 API interactions
 *
 * @author mikhail.h
 */
public interface Privat24 {

    /**
     * Retrieves exchange rates for given date
     *
     * @param date date used in request to Privat24 API
     * @return exchange rate history for specified date
     */
    ExchangeRateHistory getExchangeRatesForDate(LocalDate date);

    /**
     * Retrieves exchange rates for given date and specified currency
     *
     * @param date     {@link LocalDate} date used in request to Privat24 API
     * @param currency {@link Currency} code used in request
     * @return exchange rate history for specified date and currency
     */
    ExchangeRateHistory getExchangeRatesForDate(LocalDate date, Currency currency);

    /**
     * Retrieves exchange rates for current date
     *
     * @param exchangeRateType cash and non-cash
     * @return {@link CurrentExchangeRate}
     * @see ExchangeRateType
     */
    List<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType);

    /**
     * Retrieves exchange rates for current date and specified currency
     *
     * @param exchangeRateType cash and non-cash
     * @param currency         {@link Currency} code used in request
     * @return optional-wrapped {@link CurrentExchangeRate}
     * @see ExchangeRateType
     */
    Optional<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType, Currency currency);

    /**
     * Retrieves locations of devices in given city
     *
     * @param deviceType ATM or TSO (self-service terminal)
     * @param cityName   name of the city to search in
     * @return infrastructure location data
     */
    Infrastructure getInfrastructureLocations(DeviceType deviceType, String cityName);

    /**
     * Retrieves locations of devices in given city
     *
     * @param deviceType ATM or TSO (self-service terminal)
     * @param cityName   name of the city to search in
     * @param address    part of the address to search for (usually a street name)
     * @return infrastructure location data
     */
    Infrastructure getInfrastructureLocations(DeviceType deviceType, String cityName, String address);

}
