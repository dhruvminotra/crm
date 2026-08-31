package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "applicable_days", length = 30)
    private String applicableDays;

    @Column(length = 3, nullable = false)
    private String currency;

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

    @Column(name = "cut_off_days")
    private Integer cutOffDays;

    @Column(name = "commission_percent", precision = 7, scale = 2)
    private BigDecimal commissionPercent;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    @Column(nullable = false)
    private boolean audited = true;

    @Column(nullable = false)
    private boolean enabled = true;
}
