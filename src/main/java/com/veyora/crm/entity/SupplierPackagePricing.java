package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dated rates for a rate plan - the equivalent of tf-main SupplierPackagePricing
 * rows with type PACKAGE_PRICING (written by updateHotelRates / addHotelRates).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supplier_package_pricing")
public class SupplierPackagePricing extends BaseEntity {

    @Column(name = "rate_plan_id", nullable = false)
    private Long ratePlanId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "travel_start_date", nullable = false)
    private LocalDate travelStartDate;

    @Column(name = "travel_end_date", nullable = false)
    private LocalDate travelEndDate;

    /** Applicable days of week as CSV of MON..SUN; empty means all days. */
    @Column(name = "applicable_days", length = 30)
    private String applicableDays;

    @Column(length = 3, nullable = false)
    private String currency;

    /** Per-occupancy prices, mirroring the extranet update-rates form fields. */
    @Column(name = "single_sharing", precision = 12, scale = 2)
    private BigDecimal singleSharing;

    @Column(name = "twin_sharing", precision = 12, scale = 2)
    private BigDecimal twinSharing;

    @Column(name = "extra_adult", precision = 12, scale = 2)
    private BigDecimal extraAdult;

    @Column(name = "child_with_bed", precision = 12, scale = 2)
    private BigDecimal childWithBed;

    @Column(name = "child_without_bed", precision = 12, scale = 2)
    private BigDecimal childWithoutBed;

    @Column(precision = 12, scale = 2)
    private BigDecimal infant;

    @Column(name = "min_stay")
    private Integer minStay;

    /** Days in advance the rate must be booked (cut-off). */
    @Column(name = "cut_off_days")
    private Integer cutOffDays;

    @Column(name = "commission_percent", precision = 7, scale = 2)
    private BigDecimal commissionPercent;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    /** Rates loaded by suppliers may need auditing before going live (tf-main isAudited). */
    @Column(nullable = false)
    private boolean audited = true;

    @Column(nullable = false)
    private boolean enabled = true;
}
