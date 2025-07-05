package com.dat.backend.datshop.coupon.service;

import com.dat.backend.datshop.coupon.dto.CouponRequest;
import com.dat.backend.datshop.coupon.dto.CouponResponse;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest couponRequest, String email);

    CouponResponse applyCoupon(CouponRequest couponRequest, String name);
}
