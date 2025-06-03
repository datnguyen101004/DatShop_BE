package com.dat.backend.datshop.authentication.service.impl;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.AuthResponse;
import com.dat.backend.datshop.authentication.service.AuthService;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public AuthResponse register(LoginRequest registerRequest) {
        return null;
    }
}
