package com.dat.backend.datshop.authentication.controller;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.AuthResponse;
import com.dat.backend.datshop.authentication.service.AuthService;
import com.dat.backend.datshop.response.ApiResponse;
import com.dat.backend.datshop.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest));
    }

    // Register will redirect to home endpoint. Using same response structure as login for consistency.
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody LoginRequest registerRequest) {
        return ApiResponse.success(authService.register(registerRequest));
    }

}
