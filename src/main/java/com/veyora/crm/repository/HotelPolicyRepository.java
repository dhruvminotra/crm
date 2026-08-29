package com.veyora.crm.repository;

import com.veyora.crm.constant.PolicyType;
import com.veyora.crm.entity.HotelPolicy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelPolicyRepository extends JpaRepository<HotelPolicy, Long> {

    List<HotelPolicy> findByHotelIdAndSupplierIdAndPolicyTypeAndEnabledTrue(
            Long hotelId, Long supplierId, PolicyType policyType);

    List<HotelPolicy> findByHotelIdAndPolicyTypeAndEnabledTrue(Long hotelId, PolicyType policyType);
}
