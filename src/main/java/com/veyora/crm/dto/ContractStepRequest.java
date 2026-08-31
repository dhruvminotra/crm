package com.veyora.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class ContractStepRequest {

    @NotNull
    private Integer stepId;

    @NotNull
    private Long hotelId;

    private Long supplierId;

    private Long selectedRatePlanId;
    private RatePlanRequest ratePlan;

    private List<RateUpdateRequest> rates;

    private List<ProductPromotionOfferRequest> promotions;
}
