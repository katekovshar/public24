package com.voidaspect.public24.service.p24;

import java.util.Arrays;
import java.util.Optional;

/**
 * List of device types supported for requests
 */
public enum DeviceType {

    ATM, TSO;

    public static Optional<DeviceType> getByName(String name) {
        return Arrays.stream(values())
                .filter(deviceType -> deviceType.name().equalsIgnoreCase(name))
                .findAny();
    }

}
