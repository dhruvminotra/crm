package com.veyora.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final String token;
    private final Long userId;
    private final String displayName;
    private final String role;
}
