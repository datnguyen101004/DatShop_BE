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
            description = "Loại coupon: PERCENT (giảm giá theo %), MONEY (giảm giá theo tiền), FREE_SHIPPING (miễn phí vận chuyển)",
            example = "PERCENT"
    )
    private String couponType;
}
