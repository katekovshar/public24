package com.voidaspect.public24.service.geo;

import java.math.BigDecimal;
import java.net.URI;

public interface GMaps {

    URI getCoordinatesQuery(BigDecimal latitude, BigDecimal longitude);

}
