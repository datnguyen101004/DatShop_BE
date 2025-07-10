package com.dat.backend.datshop.coupon.service;

import com.dat.backend.datshop.coupon.dto.ApplyCoupon;
import com.dat.backend.datshop.coupon.dto.CreateCoupon;
import com.dat.backend.datshop.coupon.dto.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CreateCoupon createCoupon, String email);

    String applyCoupon(List<ApplyCoupon> applyCouponList, String email);
}
