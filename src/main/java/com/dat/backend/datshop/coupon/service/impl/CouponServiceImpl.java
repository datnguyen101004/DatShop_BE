package com.dat.backend.datshop.coupon.service.impl;

import com.dat.backend.datshop.coupon.dto.ApplyCoupon;
import com.dat.backend.datshop.coupon.dto.CreateCoupon;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponMapper couponMapper;

    @Override
    public CouponResponse createCoupon(CreateCoupon createCoupon, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Lấy các thông tin từ CreateCoupon DTO
        String code = createCoupon.getCode();
        double discountAmount = createCoupon.getDiscountAmount();
        int quantity = createCoupon.getQuantity();
        // Date format : "dd-MM-yyyy"
        String expirationDate = createCoupon.getExpirationDate();
        String couponType = createCoupon.getCouponType().toUpperCase();

        // Chuyển expirationDate từ String sang LocalDateTime
        LocalDateTime expirationDateTime = LocalDateTime.parse(expirationDate);

        //Tạo Coupon entity và lưu vào cơ sở dữ liệu
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountAmount(discountAmount);
        coupon.setQuantity(quantity);
        coupon.setExpirationDate(expirationDateTime);
        coupon.setCouponType(CouponType.valueOf(couponType.toUpperCase()));
        coupon.setAuthor(user);
        Coupon savedCoupon = couponRepository.save(coupon);
        // Map savedCoupon sang CouponResponse
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    public String applyCoupon(List<ApplyCoupon> applyCouponList, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Kiểm tra từng mã coupon trong danh sách applyCouponList
        for (ApplyCoupon applyCoupon : applyCouponList) {
            String code = applyCoupon.getCouponCode();
            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Coupon not found with code: " + code));

            // Kiểm tra xem coupon đã hết hạn hay chưa
            if (coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                return "Coupon " + code + " has expired.";
            }

            // Kiểm tra xem coupon đã được sử dụng hết hay chưa
            if (coupon.getQuantity() <= 0) {
                return "Coupon " + code + " has been used up.";
            }

            // Giảm số lượng coupon khi áp dụng thành công
            coupon.setQuantity(coupon.getQuantity() - 1);
            couponRepository.save(coupon);
        }
        return "Đã áp dụng mã giảm giá thành công.";
    }
}
