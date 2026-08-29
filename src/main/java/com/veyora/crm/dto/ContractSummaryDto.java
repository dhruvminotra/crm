package com.veyora.crm.dto;

import java.time.LocalDate;
import lombok.Data;

/** Per-hotel contracting summary (tf-main HotelContractingSummaryData). */
@Data
public class ContractSummaryDto {

    private Long hotelId;
    private String hotelName;
    private int audited;
    private int unaudited;
    private int promotions;
    private LocalDate lastValidDate;
}
