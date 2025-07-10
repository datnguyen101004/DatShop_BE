package com.dat.backend.datshop.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCoupon {
    private String code;
    @Schema(
            description = "Nhập giá trị phần trăm muốn giảm",
            example = "10.5"
    )
    private double discountAmount;
    private int quantity;
    @Schema(
            description = "Định dạng: dd-MM-yyyy",
            example = "31-12-2025"
    )
    private String expirationDate;
    @Schema(
            description = "Loại coupon: LIMIT (quantity limit), NO LIMIT, DATE LIMIT",
            example = "NO LIMIT"
    )
    private String couponType;
}
