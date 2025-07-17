package com.dat.backend.datshop.coupon.service.impl;

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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
        String code = RandomStringUtils.randomAlphanumeric(8);
        double discountAmount = createCoupon.getDiscountAmount();
        int quantity = createCoupon.getQuantity();
        // Date format : "dd-MM-yyyy"
        String expirationDateStr = createCoupon.getExpirationDate();
        String couponType = createCoupon.getCouponType().toUpperCase();

        // Chuyển expirationDate từ String sang LocalDate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate expirationDate = LocalDate.parse(expirationDateStr, dateFormatter);
        // Chuyển LocalDate sang LocalDateTime
        LocalDateTime expirationDateTime = expirationDate.atStartOfDay();

        //Tạo Coupon entity và lưu vào cơ sở dữ liệu
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountAmount(discountAmount);
        coupon.setQuantity(quantity);
        coupon.setExpirationDate(expirationDateTime);
        coupon.setCouponType(CouponType.valueOf(couponType));
        coupon.setAuthor(user);
        coupon.setIsActive(true); // Mặc định là active khi tạo mới
        // Logic tạo schedule cho coupon cho active khi đến thời hạn cho coupon

        Coupon savedCoupon = couponRepository.save(coupon);
        // Map savedCoupon sang CouponResponse
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        List<Coupon> coupons = couponRepository.findAll();
        if (!coupons.isEmpty()) {
            return coupons.stream().map(couponMapper::toCouponResponse).collect(Collectors.toList());
        }
        return null;
    }
}
