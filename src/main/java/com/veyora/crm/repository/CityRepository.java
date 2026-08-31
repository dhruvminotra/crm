package com.veyora.crm.repository;

import com.veyora.crm.entity.City;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByEnabledTrueOrderByName();

    List<City> findByCountryIdAndEnabledTrueOrderByName(Integer countryId);
}
