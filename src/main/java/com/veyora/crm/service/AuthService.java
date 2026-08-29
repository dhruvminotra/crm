package com.veyora.crm.service;

import com.veyora.crm.dto.LoginRequest;
import com.veyora.crm.dto.LoginResponse;
import com.veyora.crm.entity.AppUser;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.AppUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmailAndEnabledTrue(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getId(), user.getDisplayName(), user.getRole().name());
    }

    /** The logged-in user for the current request (set by JwtAuthFilter). */
    public static AppUser getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        return (principal instanceof AppUser user) ? user : null;
    }
}
