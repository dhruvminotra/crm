package com.veyora.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CountryRequest {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 2, max = 2)
    private String code;

    private String currency;

    private Boolean enabled;
}
