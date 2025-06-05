package com.dat.backend.datshop.authentication.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LoginResponse extends TokenResponse {
    private Long userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private boolean active;
    private String role;
    private String avatarUrl;
}
