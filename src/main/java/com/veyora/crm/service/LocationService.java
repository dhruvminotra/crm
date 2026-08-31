package com.veyora.crm.service;

import com.veyora.crm.dto.CityRequest;
import com.veyora.crm.dto.CountryRequest;
import com.veyora.crm.entity.City;
import com.veyora.crm.entity.Country;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.exceptionhandler.NotFoundException;
import com.veyora.crm.repository.CityRepository;
import com.veyora.crm.repository.CountryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public LocationService(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public List<Country> listCountries() {
        return countryRepository.findByEnabledTrueOrderByName();
    }

    @Transactional
    public Country saveCountry(CountryRequest request) {
        Country country = request.getId() != null
                ? countryRepository.findById(request.getId())
                        .orElseThrow(() -> new NotFoundException("Country " + request.getId() + " not found"))
                : new Country();

        country.setName(request.getName());
        country.setCode(request.getCode().toUpperCase());
        country.setCurrency(request.getCurrency());
        if (request.getEnabled() != null) {
            country.setEnabled(request.getEnabled());
        }
        return countryRepository.save(country);
    }

    public List<City> listCities(Integer countryId) {
        return countryId != null
                ? cityRepository.findByCountryIdAndEnabledTrueOrderByName(countryId)
                : cityRepository.findByEnabledTrueOrderByName();
    }

    public City getCity(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new NotFoundException("City " + cityId + " not found"));
    }

    @Transactional
    public City saveCity(CityRequest request) {
        countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new BadRequestException("Country " + request.getCountryId() + " not found"));

        City city = request.getId() != null
                ? cityRepository.findById(request.getId())
                        .orElseThrow(() -> new NotFoundException("City " + request.getId() + " not found"))
                : new City();

        city.setName(request.getName());
        city.setState(request.getState());
        city.setCountryId(request.getCountryId());
        if (request.getEnabled() != null) {
            city.setEnabled(request.getEnabled());
        }
        return cityRepository.save(city);
    }
}
