package com.veyora.crm.entity;

import com.veyora.crm.constant.PolicyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cancellation / commission policy for a hotel + supplier + travel window,
 * equivalent of tf-main HotelPolicy (savePolicy).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hotel_policy")
public class HotelPolicy extends BaseEntity {

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 20)
    private PolicyType policyType;

    @Column(name = "travel_start_date")
    private LocalDate travelStartDate;

    @Column(name = "travel_end_date")
    private LocalDate travelEndDate;

    /** Cancellation rules serialized as JSON (tf-main policyStr). */
    @Column(name = "policy_json", columnDefinition = "TEXT")
    private String policyJson;

    /** Populated for COMMISSION policies. */
    @Column(name = "commission_percent", precision = 7, scale = 2)
    private BigDecimal commissionPercent;

    @Column(nullable = false)
    private boolean enabled = true;
}
