package com.veyora.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CityRequest {

    private Integer id;

    @NotBlank
    private String name;

    private String state;

    @NotNull
    private Integer countryId;

    private Boolean enabled;
}
