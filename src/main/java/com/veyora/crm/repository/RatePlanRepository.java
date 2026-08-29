package com.veyora.crm.repository;

import com.veyora.crm.entity.RatePlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    List<RatePlan> findByHotelIdAndEnabledTrue(Long hotelId);

    List<RatePlan> findByHotelIdAndSupplierIdAndEnabledTrue(Long hotelId, Long supplierId);

    List<RatePlan> findByRoomIdAndEnabledTrue(Long roomId);
}
