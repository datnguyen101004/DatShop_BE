package com.dat.backend.datshop.order.entity;

import com.dat.backend.datshop.coupon.entity.Coupon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String note; // Ghi chú của người dùng về đơn hàng (ví dụ: yêu cầu giao hàng, ghi chú đặc biệt, v.v.)

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // Trạng thái đơn hàng (ví dụ: đang xử lý, đã tạo mã tính tiền, đã thanh toán, đã giao hàng, đã hủy, v.v.)

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // Phương thức thanh toán (ví dụ: chuyển khoản, tiền mặt khi nhận hàng, v.v.)

    @Enumerated(EnumType.STRING)
    private RequiredNote requiredNote; // Ghi chú về yêu cầu giao hàng (ví dụ: không cho xem hàng, cho xem hàng, v.v.)

    private Long totalPrice; // Tổng giá trị của đơn hàng

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Coupons được ap dụng cho đơn hàng
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
