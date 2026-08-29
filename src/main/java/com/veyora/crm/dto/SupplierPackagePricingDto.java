package com.veyora.crm.dto;

import com.veyora.crm.entity.SupplierPackagePricing;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class SupplierPackagePricingDto {

    private final Long id;
    private final Long ratePlanId;
    private final LocalDate travelStartDate;
    private final LocalDate travelEndDate;
    private final String applicableDays;
    private final String currency;
    private final BigDecimal singleSharing;
    private final BigDecimal twinSharing;
    private final BigDecimal extraAdult;
    private final BigDecimal childWithBed;
    private final BigDecimal childWithoutBed;
    private final Integer minStay;
    private final Integer cutOffDays;
    private final BigDecimal commissionPercent;
    private final boolean audited;

    public SupplierPackagePricingDto(SupplierPackagePricing r) {
        this.id = r.getId();
        this.ratePlanId = r.getRatePlanId();
        this.travelStartDate = r.getTravelStartDate();
        this.travelEndDate = r.getTravelEndDate();
        this.applicableDays = r.getApplicableDays();
        this.currency = r.getCurrency();
        this.singleSharing = r.getSingleSharing();
        this.twinSharing = r.getTwinSharing();
        this.extraAdult = r.getExtraAdult();
        this.childWithBed = r.getChildWithBed();
        this.childWithoutBed = r.getChildWithoutBed();
        this.minStay = r.getMinStay();
        this.cutOffDays = r.getCutOffDays();
        this.commissionPercent = r.getCommissionPercent();
        this.audited = r.isAudited();
    }
}
