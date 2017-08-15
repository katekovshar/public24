package com.voidaspect.public24.service.p24;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Privat24 public API integration
 * @author mikhail.h
 */
@Component
@ConfigurationProperties("privat24")
@Getter
@Setter
public final class Privat24Properties {

    /**
     * URL of Privat24 API
     */
    private String url;

    /**
     * Format of requests (usually json)
     */
    private String format;

}
