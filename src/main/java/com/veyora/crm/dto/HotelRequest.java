package com.veyora.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelRequest {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Integer cityId;

    private Integer starRating;

    private boolean cruise;

    private Boolean enabled;
}
