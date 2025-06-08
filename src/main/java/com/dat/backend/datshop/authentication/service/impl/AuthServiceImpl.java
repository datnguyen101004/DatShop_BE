package com.dat.backend.datshop.authentication.service.impl;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.LoginResponse;
import com.dat.backend.datshop.authentication.dto.RegisterRequest;
import com.dat.backend.datshop.authentication.dto.TokenResponse;
import com.dat.backend.datshop.authentication.service.AuthService;
import com.dat.backend.datshop.authentication.service.JwtService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import com.dat.backend.datshop.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    @Override
    public String register(RegisterRequest registerRequest) {
        // Check user existence
        String email = registerRequest.getEmail();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.warn("User with email {} already exists", email);
            throw new RuntimeException("User already exists");
        }

        // Create new user if not exists
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setName(registerRequest.getName());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setAvatarUrl(registerRequest.getAvatarUrl());
        newUser.setActive(true);
        newUser.setRole(UserRole.USER);

        // Save new user
        userRepository.save(newUser);
        log.info("User registered successfully with email: {}", email);

        // Send activation email
        // TODO: Implement email sending logic here
        return "Registration successful. Please check your email to activate your account.";
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        // Implement redis to blacklist JWT accessToken
        log.info("Refreshing token for access token: {}", refreshToken);

        // Validate refresh token
        String email = jwtService.extractUsername(refreshToken);
        // Check if refresh token is expired
        if (jwtService.isTokenExpired(refreshToken)) {
            log.warn("Refresh token expired");
            throw new RuntimeException("Refresh token expired");
        }
        // Check if user not exists
        if (email == null || userRepository.findByEmail(email).isEmpty()) {
            log.warn("Invalid refresh token for email: {}", email);
            throw new RuntimeException("Invalid refresh token");
        }
        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(email);
        log.info("Generated new access token for user: {}", email);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Assuming refresh token remains the same
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Validate user credentials
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        log.info("User {} logged in successfully", email);

        // If authentication is successful, fetch user details
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // If user is not active
        if (!user.isActive()) {
            log.warn("User {} is not active", email);
            throw new RuntimeException("User account is not active");
        }

        // Generate JWT token
        String accessToken = jwtService.generateAccessToken(email);
        log.info("Generated JWT token for user: {}", email);
        String refreshToken = jwtService.generateRefreshToken(email);
        log.info("Generated refresh token for user: {}", email);

        // Return user details and tokens
        return LoginResponse.builder()
                .userId(user.getId())
                .email(email)
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .active(user.isActive())
                .build();
    }
}
