package com.dat.backend.datshop.coupon.service;

import com.dat.backend.datshop.coupon.dto.CreateCoupon;
import com.dat.backend.datshop.coupon.dto.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CreateCoupon createCoupon, String email);

    List<CouponResponse> getAllCoupons(String name);
}
