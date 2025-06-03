package com.dat.backend.datshop.authentication.service;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);

    AuthResponse register(LoginRequest registerRequest);
}
