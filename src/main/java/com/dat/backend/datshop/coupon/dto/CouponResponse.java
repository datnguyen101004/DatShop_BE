package com.dat.backend.datshop.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String couponType;
    private double discountAmount;
    private int quantity;
    private int usedCount;
    private boolean isActive;
    private LocalDateTime expirationDate;
}
