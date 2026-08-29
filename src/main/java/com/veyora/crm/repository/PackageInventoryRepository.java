package com.veyora.crm.repository;

import com.veyora.crm.entity.PackageInventory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageInventoryRepository extends JpaRepository<PackageInventory, Long> {

    List<PackageInventory> findByHotelIdAndSupplierIdAndStayDateBetween(
            Long hotelId, Long supplierId, LocalDate from, LocalDate to);

    List<PackageInventory> findByHotelIdAndRoomIdAndSupplierIdAndStayDateBetween(
            Long hotelId, Long roomId, Long supplierId, LocalDate from, LocalDate to);

    List<PackageInventory> findByHotelIdInAndStayDateBetween(List<Long> hotelIds, LocalDate from, LocalDate to);
}
