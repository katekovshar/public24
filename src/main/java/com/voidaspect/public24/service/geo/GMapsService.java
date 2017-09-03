package com.voidaspect.public24.service.geo;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@Service
public class GMapsService implements GMaps {

    @Override
    public URI getCoordinatesQuery(BigDecimal latitude, BigDecimal longitude) { //todo param validation
        return getGMapsBaseUri()
                .queryParam("query", getCoordinatesQueryString(latitude, longitude))
                .build().encode().toUri();
    }

    private String getCoordinatesQueryString(BigDecimal latitude, BigDecimal longitude) {
        return latitude.toPlainString() + ',' + longitude.toPlainString();
    }

    private UriComponentsBuilder getGMapsBaseUri() {
        return UriComponentsBuilder.fromHttpUrl("https://www.google.com/maps/search/")
                .queryParam("api", 1);
    }

}
