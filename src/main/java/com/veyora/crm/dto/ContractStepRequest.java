package com.veyora.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * The 4-step add-contract wizard payload (tf-main /hotels/contract-x, moveContractStep):
 * step 1 = select/create rate plan, step 2 = load rates, step 3 = promotions, step 4 = supplements.
 */
@Data
public class ContractStepRequest {

    @NotNull
    private Integer stepId;

    @NotNull
    private Long hotelId;

    private Long supplierId;

    /** Step 1: existing plan id, or a new plan definition. */
    private Long selectedRatePlanId;
    private RatePlanRequest ratePlan;

    /** Step 2: one or more rate windows for the selected plan. */
    private List<RateUpdateRequest> rates;

    /** Step 3: promotions to add. */
    private List<ProductPromotionOfferRequest> promotions;
}
