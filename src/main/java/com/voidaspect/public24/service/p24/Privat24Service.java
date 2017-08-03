package com.voidaspect.public24.service.p24;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author mikhail.h
 */
@Service
public final class Privat24Service implements Privat24 {

    private static final String URI_TEMPLATE = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";

    private final RestTemplate restTemplate;

    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public Privat24Service(RestTemplateBuilder restTemplateBuilder, DateTimeFormatter dateTimeFormatter) {
        restTemplate = restTemplateBuilder
                .build();
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public ExchangeRateData getExchangeRatesForDate(LocalDate date) {
        URI uri = URI.create(URI_TEMPLATE +
                date.format(dateTimeFormatter));

        return restTemplate.getForObject(uri, ExchangeRateData.class);
    }

    @Override
    public Optional<ExchangeRate> getExchangeRatesForDate(LocalDate date, Currency currency) {
        return getExchangeRatesForDate(date).getExchangeRates()
                .stream()
                .filter(exchangeRate -> exchangeRate.getCurrency().equals(currency.name()))
                .findAny();
    }
}
