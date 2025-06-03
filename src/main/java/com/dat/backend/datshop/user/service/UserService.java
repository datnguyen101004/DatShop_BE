package com.dat.backend.datshop.user.service;

import com.dat.backend.datshop.user.dto.UserResponse;

public interface UserService {
    UserResponse getProfile(String email);
}
