package com.veyora.crm.repository;

import com.veyora.crm.entity.MarketPlaceHotel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketPlaceHotelRepository extends JpaRepository<MarketPlaceHotel, Long> {

    List<MarketPlaceHotel> findByEnabledTrue();

    List<MarketPlaceHotel> findByCityIdAndEnabledTrue(Integer cityId);

    List<MarketPlaceHotel> findByIdInAndEnabledTrue(List<Long> ids);
}
