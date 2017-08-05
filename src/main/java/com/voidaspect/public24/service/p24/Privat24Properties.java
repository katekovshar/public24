package com.voidaspect.public24.service.p24;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mikhail.h
 */
@Component
@ConfigurationProperties("privat24")
@Getter
@Setter
public final class Privat24Properties {

    private String url;

    private String format;

}
