package com.veyora.crm.dto;

import com.veyora.crm.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

    private Long userId;

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotBlank
    private String displayName;

    @NotNull
    private RoleType role;

    private String businessCurrency;
}
