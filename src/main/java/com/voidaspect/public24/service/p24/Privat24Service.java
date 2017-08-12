package com.voidaspect.public24.service.p24;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mikhail.h
 */
@Service
@Slf4j
public final class Privat24Service implements Privat24 {

    private final RestTemplate restTemplate;

    private final DateTimeFormatter dateTimeFormatter;

    private final Privat24Properties privat24Properties;

    private final Map<LocalDate, ExchangeRateHistory> exchangeHistoryCache =
            new HashMap<>();

    @Autowired
    public Privat24Service(RestTemplateBuilder restTemplateBuilder,
                           DateTimeFormatter dateTimeFormatter,
                           Privat24Properties privat24Properties) {
        this.restTemplate = restTemplateBuilder.build();
        this.dateTimeFormatter = dateTimeFormatter;
        this.privat24Properties = privat24Properties;
    }

    @Override
    public ExchangeRateHistory getExchangeRatesForDate(LocalDate date) {
        return exchangeHistoryCache.computeIfAbsent(date, d -> {
            val uri = getUriComponentsBuilder()
                    .path("/exchange_rates")
                    .queryParam("date", date.format(dateTimeFormatter))
                    .build().toUri();
            log.debug("GET Request to p24 api: {}", uri);
            return restTemplate.getForObject(uri, ExchangeRateHistory.class);
        });
    }

    @Override
    public ExchangeRateHistory getExchangeRatesForDate(LocalDate date, Currency currency) {
        ExchangeRateHistory exchangeRatesForDate = getExchangeRatesForDate(date);

        List<ExchangeRateHistoryCurrency> ccyHistory = exchangeRatesForDate.getExchangeRates().stream()
                .filter(exchangeRate -> exchangeRate.getCurrency().equals(currency.name()))
                .collect(Collectors.toList());

        return new ExchangeRateHistory(
                exchangeRatesForDate.getDate(),
                exchangeRatesForDate.getBank(),
                exchangeRatesForDate.getBaseCurrency(),
                exchangeRatesForDate.getBaseCurrencyLit(),
                ccyHistory);
    }

    @Override
    public List<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType) {
        val uri = getUriComponentsBuilder()
                .path("/pubinfo")
                .queryParam("exchange")
                .queryParam("coursid", exchangeRateType.getId())
                .build().toUri();
        log.debug("GET Request to p24 api: {}", uri);
        CurrentExchangeRate[] rates = restTemplate.getForObject(uri, CurrentExchangeRate[].class);
        return Arrays.asList(rates);
    }

    @Override
    public Optional<CurrentExchangeRate> getCurrentExchangeRates(ExchangeRateType exchangeRateType, Currency currency) {
        return getCurrentExchangeRates(exchangeRateType).stream()
                .filter(exchangeRate -> exchangeRate.getCurrency().equals(currency.name()))
                .findAny();
    }

    private UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder.fromHttpUrl(privat24Properties.getUrl())
                .queryParam(privat24Properties.getFormat());
    }
}
