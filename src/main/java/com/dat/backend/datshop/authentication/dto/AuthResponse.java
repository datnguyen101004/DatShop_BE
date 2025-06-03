package com.dat.backend.datshop.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private boolean active;
    private String role;
    private String avatarUrl;
}
