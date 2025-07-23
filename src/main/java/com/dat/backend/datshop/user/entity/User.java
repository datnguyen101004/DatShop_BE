package com.dat.backend.datshop.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private UserRole role;
    private boolean isActive;
    private String avatarUrl;
    private Double balance = 0.0;

    // Địa chỉ
    private String address; // địa chỉ cụ thể (ví dụ: số nhà, đường phố)
    private String wardName; // Tên phường/xã
    private String districtName; // Tên quận/huyện
    private String provinceName; // Tên tỉnh/thành phố

    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDateTime updatedAt;
}
