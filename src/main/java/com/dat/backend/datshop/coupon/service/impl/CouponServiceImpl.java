package com.dat.backend.datshop.coupon.service.impl;

import com.dat.backend.datshop.coupon.dto.CouponRequest;
import com.dat.backend.datshop.coupon.dto.CouponResponse;
import com.dat.backend.datshop.coupon.entity.Coupon;
import com.dat.backend.datshop.coupon.entity.CouponType;
import com.dat.backend.datshop.coupon.mapper.CouponMapper;
import com.dat.backend.datshop.coupon.repository.CouponRepository;
import com.dat.backend.datshop.coupon.service.CouponService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponMapper couponMapper;

    @Override
    public CouponResponse createCoupon(CouponRequest couponRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Get ìinformation from couponRequest and create a new coupon entity
        String code = couponRequest.getCode();
        double discountAmount = couponRequest.getDiscountAmount();
        int quantity = couponRequest.getQuantity();
        // Date format : "dd-MM-yyyy"
        String expirationDate = couponRequest.getExpirationDate();
        String couponType = couponRequest.getCouponType();

        // Convert expirationDate to a Date object
        LocalDateTime expirationDateTime = LocalDateTime.parse(expirationDate);

        // Create and save the coupon entity
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountAmount(discountAmount);
        coupon.setQuantity(quantity);
        coupon.setExpirationDate(expirationDateTime);
        coupon.setCouponType(CouponType.valueOf(couponType.toUpperCase()));
        coupon.setAuthor(user);
        Coupon savedCoupon = couponRepository.save(coupon);
        // Map the saved coupon entity to a response DTO
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    public CouponResponse applyCoupon(CouponRequest couponRequest, String name) {
        return null;
    }
}
