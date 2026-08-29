package com.veyora.crm.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * One row of the hotel contracts dashboard: a hotel-supplier mapping plus its
 * contracting summary (tf-main hotel_contracts_manage.jsp row +
 * HotelContractingSummaryData).
 */
@Data
public class ContractSummaryDto {

    private Long mappingId;
    private Long hotelId;
    private String hotelName;
    private Integer cityId;
    private String cityName;
    private Long supplierId;
    private String supplierName;
    private int audited;
    private int unaudited;
    private int promotions;
    private LocalDate lastValidDate;
}
