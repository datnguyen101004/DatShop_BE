package com.dat.backend.datshop.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyCoupon {
    private List<String> couponCode;
    private Long billId;
    private LocalDateTime usedAt;
}
