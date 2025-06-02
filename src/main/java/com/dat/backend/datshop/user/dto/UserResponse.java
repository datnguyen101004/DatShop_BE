package com.dat.backend.datshop.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private boolean active;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
