package com.veyora.crm.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class HotelSearchResultDto {

    private Long hotelId;
    private String hotelName;
    private Integer cityId;
    private String cityName;
    private String countryCode;
    private String countryName;
    private Integer starRating;
    private int nights;
    private String currency;
    private BigDecimal lowestTotalPrice;
    private BigDecimal lowestPerNightPrice;
    private List<HotelSearchRoomOptionDto> roomOptions;
}
