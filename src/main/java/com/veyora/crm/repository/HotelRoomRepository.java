package com.veyora.crm.repository;

import com.veyora.crm.entity.HotelRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {

    List<HotelRoom> findByHotelIdAndEnabledTrue(Long hotelId);
}
