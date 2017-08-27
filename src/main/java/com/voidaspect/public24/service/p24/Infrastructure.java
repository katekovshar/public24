package com.voidaspect.public24.service.p24;

import lombok.Data;

import java.util.List;

@Data
public final class Infrastructure {

    private String city;

    private String address;

    private List<Device> devices;

}
