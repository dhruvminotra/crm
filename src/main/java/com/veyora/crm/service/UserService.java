package com.veyora.crm.service;

import com.veyora.crm.constant.RoleType;
import com.veyora.crm.dto.DeskUserRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.exceptionhandler.NotFoundException;
import com.veyora.crm.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelDataService hotelDataService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            HotelDataService hotelDataService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hotelDataService = hotelDataService;
    }

    public List<User> searchDeskUsers(User loggedInUser, String partialNameEmail,
            Boolean isActiveUsers, RoleType roleType) {
        String activated = isActiveUsers == null ? null
                : (isActiveUsers ? User.ACTIVATED_YES : User.ACTIVATED_NO);
        String prefix = (partialNameEmail == null || partialNameEmail.isBlank()) ? null
                : partialNameEmail.trim().toLowerCase() + "%";

        boolean isSystemUser = hotelDataService.isSystemUser(loggedInUser);
        RoleType effectiveRole = isSystemUser ? roleType : RoleType.DESK_USER;

        List<User> users = userRepository.searchUsers(effectiveRole, activated, prefix);
        if (!isSystemUser) {
            users = users.stream()
                    .filter(u -> loggedInUser.getUserId().equals(u.getPageOwnerId()))
                    .toList();
        }
        return users;
    }

    @Transactional
    public User saveDeskUserDetails(DeskUserRequest request, User loggedInUser) {
        boolean isSystemUser = hotelDataService.isSystemUser(loggedInUser);

        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("User " + request.getUserId() + " not found"));

            if (!isSystemUser && !loggedInUser.getUserId().equals(user.getPageOwnerId())
                    && !loggedInUser.getUserId().equals(user.getUserId())) {
                throw new BadRequestException("You are not authorized to perform this operation");
            }
        } else {
            user = new User();
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new BadRequestException("Password is required for a new user");
            }
        }

        userRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> !existing.getUserId().equals(user.getUserId()))
                .ifPresent(existing -> {
                    throw new BadRequestException("A user with this email already exists");
                });

        RoleType roleType = request.getRoleType() != null && isSystemUser
                ? request.getRoleType()
                : (user.getUserId() != null ? user.getRoleType() : RoleType.DESK_USER);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setRoleType(roleType);
        user.setRole(roleType.getRoleChar());
        if (user.getUserId() == null) {
            Long deskAdminId = (isSystemUser && request.getAdminUserId() != null)
                    ? request.getAdminUserId()
                    : loggedInUser.getUserId();
            user.setPageOwnerId(deskAdminId);
            user.setBaseCurrency(loggedInUser.getBaseCurrency());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getActivated() != null) {
            user.setActivated(request.getActivated() ? User.ACTIVATED_YES : User.ACTIVATED_NO);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User saveDeskUserStatus(Long userId, boolean activated, User loggedInUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        if (!hotelDataService.isSystemUser(loggedInUser)
                && !loggedInUser.getUserId().equals(user.getPageOwnerId())) {
            throw new BadRequestException("You are not authorized to perform this operation");
        }
        user.setActivated(activated ? User.ACTIVATED_YES : User.ACTIVATED_NO);
        return userRepository.save(user);
    }
}
