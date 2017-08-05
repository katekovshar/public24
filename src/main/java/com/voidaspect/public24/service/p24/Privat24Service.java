package com.voidaspect.public24.service.p24;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author mikhail.h
 */
@Service
public final class Privat24Service implements Privat24 {

    private final RestTemplate restTemplate;

    private final DateTimeFormatter dateTimeFormatter;

    private final Privat24Properties privat24Properties;

    @Autowired
    public Privat24Service(RestTemplateBuilder restTemplateBuilder,
                           DateTimeFormatter dateTimeFormatter,
                           Privat24Properties privat24Properties) {
        this.restTemplate = restTemplateBuilder.build();
        this.dateTimeFormatter = dateTimeFormatter;
        this.privat24Properties = privat24Properties;
    }

    @Override
    public ExchangeRateData getExchangeRatesForDate(LocalDate date) {
        URI uri = UriComponentsBuilder.fromHttpUrl(privat24Properties.getUrl())
                .path("/exchange_rates")
                .queryParam(privat24Properties.getFormat())
                .queryParam("date", date.format(dateTimeFormatter))
                .build().toUri();

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
