package com.veyora.crm.repository;

import com.veyora.crm.entity.MarketPlaceHotel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarketPlaceHotelRepository extends JpaRepository<MarketPlaceHotel, Long> {

    List<MarketPlaceHotel> findByEnabledTrue();

    List<MarketPlaceHotel> findByCityIdAndEnabledTrue(Integer cityId);

    List<MarketPlaceHotel> findByIdInAndEnabledTrue(List<Long> ids);

    /** tf-main MiscAction.HOTEL_SUGGEST. */
    List<MarketPlaceHotel> findTop10ByNameStartingWithIgnoreCaseAndEnabledTrue(String prefix);

    /** tf-main MiscAction.CITY_SUGGEST (cities come from the hotel catalog here). */
    @Query("select distinct h.cityId, h.cityName from MarketPlaceHotel h "
            + "where h.enabled = true and lower(h.cityName) like :prefix order by h.cityName")
    List<Object[]> suggestCities(@Param("prefix") String prefix);
}
