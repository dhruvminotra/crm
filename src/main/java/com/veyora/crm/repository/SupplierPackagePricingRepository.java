package com.veyora.crm.repository;

import com.veyora.crm.entity.SupplierPackagePricing;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierPackagePricingRepository extends JpaRepository<SupplierPackagePricing, Long> {

    List<SupplierPackagePricing> findByRatePlanIdAndEnabledTrue(Long ratePlanId);

    Optional<SupplierPackagePricing> findByRatePlanIdAndTravelStartDateAndTravelEndDateAndEnabledTrue(
            Long ratePlanId, LocalDate travelStartDate, LocalDate travelEndDate);

    /** All rates overlapping a stay window for a hotel (the inventory-x grid load). */
    @Query("select r from SupplierPackagePricing r where r.hotelId = :hotelId and r.enabled = true "
            + "and r.travelStartDate <= :endDate and r.travelEndDate >= :startDate")
    List<SupplierPackagePricing> findOverlapping(@Param("hotelId") Long hotelId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /** Commission cascade target set (tf-main updateCommissionsForAllRates). */
    @Query("select r from SupplierPackagePricing r where r.hotelId = :hotelId and r.supplierId = :supplierId "
            + "and r.enabled = true and r.travelStartDate <= :endDate and r.travelEndDate >= :startDate")
    List<SupplierPackagePricing> findForCommissionCascade(@Param("hotelId") Long hotelId,
                                            @Param("supplierId") Long supplierId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
