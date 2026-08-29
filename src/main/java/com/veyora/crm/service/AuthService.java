package com.veyora.crm.service;

import com.veyora.crm.dto.LoginRequest;
import com.veyora.crm.dto.LoginResponse;
import com.veyora.crm.entity.User;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        if (!user.isActivatedUser()
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getRoleType().name());
        return new LoginResponse(token, user.getUserId(), user.getName(), user.getRoleType().name());
    }

    /** The logged-in user for the current request (set by JwtAuthFilter). */
    public static User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        return (principal instanceof User user) ? user : null;
    }
}
