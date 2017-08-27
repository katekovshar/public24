package com.voidaspect.public24.service.p24;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;

@Data
public final class Device {

    @SerializedName("type")
    private DeviceType deviceType;

    private String PlaceRu;

    private String fullAddressRu;

    private String placeUa;

    private String fullAddressUa;

    private String fullAddressEn;

    private String cityRU;

    private String cityUA;

    private String cityEN;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @SerializedName("tw")
    private HashMap<String, String> workSchedule;

}
