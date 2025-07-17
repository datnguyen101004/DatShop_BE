package com.dat.backend.datshop.coupon.entity;

import com.dat.backend.datshop.payment.entity.Bill;
import com.dat.backend.datshop.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;
    private String code;
    private Double discountAmount;
    private Boolean isActive;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @ManyToOne
    private User author;

    @ManyToOne
    private Bill bill;

    private LocalDateTime expirationDate;
}
