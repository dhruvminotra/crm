package com.veyora.crm.dto;

import com.veyora.crm.constant.ContractType;
import com.veyora.crm.constant.MealPlan;
import com.veyora.crm.constant.RoomOccupancy;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/** Mirrors the tf-main add/edit rate plan form (addRatePlan). */
@Data
public class RatePlanRequest {

    private Long ratePlanId; // null = create

    @NotNull
    private Long roomId;

    private Long supplierId;

    @NotNull
    private MealPlan mealPlan;

    @NotNull
    private String currency;

    /** Occupancy template; CUSTOM uses the explicit fields below. */
    private RoomOccupancy roomOccupancy;

    private Integer occupancy;
    private Integer maxAdults;
    private Integer maxChildWithMaxAdults;
    private Integer maxChildWithoutBed;

    private Integer minChildWithBedAge;
    private Integer minChildWithoutBedAge;
    private Integer minAdultAge;
    private Integer minLengthOfStay;

    private String promoCode;
    private String inclusions;

    private ContractType contractType;
    private BigDecimal commissionPercent;

    /** Cancellation rules as JSON string, stored verbatim like tf-main policyStr. */
    private String cancellationPolicyJson;
}
