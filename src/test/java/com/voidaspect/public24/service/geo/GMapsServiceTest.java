package com.voidaspect.public24.service.geo;

import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;

import static org.junit.Assert.*;

public class GMapsServiceTest {

    private final GMapsService gMapsService = new GMapsService();

    @Test
    public void givenCoordinates_whenGetCoordinatesQueryCalled_thenReturnValidURI() throws Exception {
        assertCoordinatesQuery("49.957833", "36.357359");
        assertCoordinatesQuery("32", "-85");
        assertCoordinatesQuery("0", "0");
    }

    private void assertCoordinatesQuery(String latitude, String longitude) {
        URI coordinatesQuery = gMapsService.getCoordinatesQuery(new BigDecimal(latitude), new BigDecimal(longitude));
        assertEquals("https://www.google.com/maps/search/?api=1&query=" + latitude + ',' + longitude, String.valueOf(coordinatesQuery));
        System.out.println(coordinatesQuery);
    }
}