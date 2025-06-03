package com.dat.backend.datshop.authentication.service;

import com.dat.backend.datshop.authentication.dto.LoginRequest;
import com.dat.backend.datshop.authentication.dto.LoginResponse;
import com.dat.backend.datshop.authentication.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);
}
