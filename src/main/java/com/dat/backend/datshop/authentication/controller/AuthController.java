package com.dat.backend.datshop.authentication.controller;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.LoginResponse;
import com.dat.backend.datshop.authentication.dto.RegisterRequest;
import com.dat.backend.datshop.authentication.dto.TokenResponse;
import com.dat.backend.datshop.authentication.service.AuthService;
import com.dat.backend.datshop.template.ApiResponse;
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
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest));
    }

    // Register will redirect to home endpoint. Using same response structure as login for consistency.
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return ApiResponse.success(authService.register(registerRequest));
    }

    // Logout
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // Implement redis to manage jwt token
        return ApiResponse.success("Logout successful");
    }

    //Refresh token
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@RequestBody String refreshToken) {
        return ApiResponse.success(authService.refreshToken(refreshToken));
    }

}
