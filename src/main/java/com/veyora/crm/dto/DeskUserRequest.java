package com.veyora.crm.dto;

import com.veyora.crm.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Mirrors the tf-main DESK_USER_ADD_UPDATE form (UserBean.saveDeskUserDetails). */
@Data
public class DeskUserRequest {

    private Long userId; // null = create

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String mobile;

    private String password;

    /** Defaults to DESK_USER as in tf-main; admins may set other role types. */
    private RoleType roleType;

    /** Desk admin (tf-main adminUId); defaults to the logged-in user. */
    private Long adminUserId;

    private Boolean activated;
}
