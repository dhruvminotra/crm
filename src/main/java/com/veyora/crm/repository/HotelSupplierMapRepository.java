package com.veyora.crm.repository;

import com.veyora.crm.entity.HotelSupplierMap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelSupplierMapRepository extends JpaRepository<HotelSupplierMap, Long> {

    List<HotelSupplierMap> findBySupplierIdAndMapType(Long supplierId, String mapType);

    List<HotelSupplierMap> findByHotelIdAndMapType(Long hotelId, String mapType);

    boolean existsByHotelIdAndSupplierIdAndMapType(Long hotelId, Long supplierId, String mapType);
}
