package com.dat.backend.datshop.coupon.controller;

import com.dat.backend.datshop.coupon.dto.CouponRequest;
import com.dat.backend.datshop.coupon.dto.CouponResponse;
import com.dat.backend.datshop.coupon.service.CouponService;
import com.dat.backend.datshop.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/create")
    public ApiResponse<CouponResponse> createCoupon(@RequestBody CouponRequest couponRequest, Authentication authentication) {
        return ApiResponse.success(couponService.createCoupon(couponRequest, authentication.getName()));
    }

    @PostMapping("/apply")
    public ApiResponse<CouponResponse> applyCoupon(@RequestBody CouponRequest couponRequest, Authentication authentication) {
        return ApiResponse.success(couponService.applyCoupon(couponRequest, authentication.getName()));
    }
}
