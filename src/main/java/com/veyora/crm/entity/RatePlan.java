package com.veyora.crm.entity;

import com.veyora.crm.constant.ContractType;
import com.veyora.crm.constant.MealPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A sellable room + meal-plan combination, the equivalent of tf-main
 * SupplierPackagePricing rows with type RATE_PLAN (created by addRatePlan).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rate_plan")
public class RatePlan extends BaseEntity {

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    /** e.g. "Deluxe Room - CP" (+ " - NRF" for non refundable), as built in tf-main. */
    @Column(name = "plan_name", nullable = false, length = 200)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_plan", nullable = false, length = 10)
    private MealPlan mealPlan;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(name = "max_occupancy", nullable = false)
    private int maxOccupancy;

    @Column(name = "max_adults", nullable = false)
    private int maxAdults;

    @Column(name = "max_child_with_max_adults")
    private Integer maxChildWithMaxAdults;

    @Column(name = "max_child_without_bed")
    private Integer maxChildWithoutBed;

    @Column(name = "min_cwb_age")
    private Integer minChildWithBedAge;

    @Column(name = "min_cwob_age")
    private Integer minChildWithoutBedAge;

    @Column(name = "min_adult_age")
    private Integer minAdultAge;

    @Column(name = "min_length_of_stay")
    private Integer minLengthOfStay;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    @Column(length = 1000)
    private String inclusions;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", length = 20)
    private ContractType contractType = ContractType.NET_RATE;

    @Column(name = "commission_percent", precision = 7, scale = 2)
    private BigDecimal commissionPercent;

    /** Cancellation policy rules serialized as JSON, as tf-main stores policyStr. */
    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicyJson;

    @Column(nullable = false)
    private boolean enabled = true;
}
