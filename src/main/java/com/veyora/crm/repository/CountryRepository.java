package com.veyora.crm.repository;

import com.veyora.crm.entity.Country;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    List<Country> findByEnabledTrueOrderByName();
}
