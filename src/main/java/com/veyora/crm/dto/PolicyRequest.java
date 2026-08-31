package com.veyora.crm.dto;

import com.veyora.crm.constant.PolicyType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PolicyRequest {

    private Long policyId;

    @NotNull
    private Long hotelId;

    private Long supplierId;

    @NotNull
    private PolicyType policyType;

    private LocalDate travelStartDate;
    private LocalDate travelEndDate;

    private String policyJson;

    private BigDecimal commissionPercent;
}
