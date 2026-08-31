package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.constant.RoleType;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.dto.DeskUserRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.service.AuthService;
import com.veyora.crm.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_V1 + "/user")
@PreAuthorize("hasAnyRole('ADMIN','CALLCENTER','SUPERVISOR','PRODUCT','BUSINESS_MANAGER',"
        + "'TOUR_OPERATOR','HOTELIER','TRAVEL_AGENT','GUEST_HOUSE')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private User user() {
        User user = AuthService.getLoggedInUser();
        if (user == null) {
            throw new BadRequestException("No logged-in user");
        }
        return user;
    }

    @GetMapping("/manage-desk-users")
    public ApiResponse<List<User>> manageDeskUsers(
            @RequestParam(name = "uemail", required = false) String partialNameEmail,
            @RequestParam(name = "uactive", required = false) Boolean isActiveUsers,
            @RequestParam(required = false) RoleType roleType) {
        return ApiResponse.ok(userService.searchDeskUsers(user(), partialNameEmail, isActiveUsers, roleType));
    }

    @PostMapping("/desk-user-add-update")
    public ApiResponse<User> deskUserAddUpdate(@Valid @RequestBody DeskUserRequest request) {
        return ApiResponse.ok("User details saved successfully!",
                userService.saveDeskUserDetails(request, user()));
    }

    @PostMapping("/save-desk-user-status")
    public ApiResponse<User> saveDeskUserStatus(@RequestParam Long userId,
            @RequestParam boolean activated) {
        return ApiResponse.ok(userService.saveDeskUserStatus(userId, activated, user()));
    }
}
