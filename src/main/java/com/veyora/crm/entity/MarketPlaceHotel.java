package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "market_place_hotel")
public class MarketPlaceHotel extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "city_name", length = 100)
    private String cityName;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "star_rating")
    private Integer starRating;

    @Column(name = "is_cruise", nullable = false)
    private boolean cruise = false;

    @Column(nullable = false)
    private boolean enabled = true;
}
