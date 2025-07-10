package com.dat.backend.datshop.coupon.controller;

import com.dat.backend.datshop.coupon.dto.ApplyCoupon;
import com.dat.backend.datshop.coupon.dto.CreateCoupon;
import com.dat.backend.datshop.coupon.dto.CouponResponse;
import com.dat.backend.datshop.coupon.service.CouponService;
import com.dat.backend.datshop.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(
            summary = "Tạo mã giảm giá mới",
            description = "Tạo mã giảm giá mới cho 1 sản phẩm bất kỳ. Chỉ chủ shop mới có quyền tạo mã giảm giá."
    )
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Mã giảm giá được tạo thành công"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Người dùng không có quyền tạo mã giảm giá"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy sản phẩm"
                    )
            }
    )
    @PostMapping("/create")
    public ApiResponse<CouponResponse> createCoupon(@RequestBody CreateCoupon createCoupon, Authentication authentication) {
        return ApiResponse.success(couponService.createCoupon(createCoupon, authentication.getName()));
    }

    @Operation(
            summary = "Áp dụng mã giảm giá",
            description = "Áp dụng mã giảm giá cho đơn hàng. Chỉ người dùng đã đăng nhập mới có thể áp dụng mã giảm giá."
    )
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Mã giảm giá được áp dụng thành công"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Mã giảm giá không hợp lệ hoặc đã hết hạn"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy mã giảm giá"
                    )
            }
    )
    @PostMapping("/apply")
    public ApiResponse<String> applyCoupon(@RequestBody List<ApplyCoupon> applyCouponList, Authentication authentication) {
        return ApiResponse.success(couponService.applyCoupon(applyCouponList, authentication.getName()));
    }
}
