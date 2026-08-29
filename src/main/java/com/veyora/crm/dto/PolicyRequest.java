package com.veyora.crm.dto;

import com.veyora.crm.constant.PolicyType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/** Mirrors the tf-main /hotels/policies-save + /hotels/commissions-save forms. */
@Data
public class PolicyRequest {

    private Long policyId; // null = create

    @NotNull
    private Long hotelId;

    private Long supplierId;

    @NotNull
    private PolicyType policyType;

    private LocalDate travelStartDate;
    private LocalDate travelEndDate;

    /** For CANCELLATION policies: rules as JSON. */
    private String policyJson;

    /** For COMMISSION policies: percentage, cascaded to live rates like tf-main. */
    private BigDecimal commissionPercent;
}
