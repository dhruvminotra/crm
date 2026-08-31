package com.veyora.crm.dto;

import com.veyora.crm.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeskUserRequest {

    private Long userId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String mobile;

    private String password;

    private RoleType roleType;

    private Long adminUserId;

    private Boolean activated;
}
