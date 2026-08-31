package com.veyora.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class RateUpdateRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long ratePlanId;

    private Long supplierId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private BigDecimal singleSharing;
    private BigDecimal twinSharing;
    private BigDecimal extraAdult;
    private BigDecimal childWithBed;
    private BigDecimal childWithoutBed;
    private BigDecimal infant;

    private Integer minStay;
    private Integer cutOffDays;
    private String promoCode;

    private Set<String> applicableDays;
}
