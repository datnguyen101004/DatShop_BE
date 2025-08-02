package com.dat.backend.datshop.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId; // ID của người nhận
    private Long shopId; // ID của shop
    private String clientOrderCode; // Mã đơn hàng của khách hàng
    private String ghnOrderCode; // Mã đơn hàng của GHN
    private String orderStatus; // Trạng thái đơn hàng (ví dụ: đang xử lý, đã giao hàng, đã hủy, v.v.)
    private Long totalFee; // Tổng phí giao hàng
    private LocalDateTime expectedDeliveryTime; // Thời gian giao hàng dự kiến
    private String note; // Ghi chú của người dùng về đơn hàng

    private String fromName;
    private String fromPhone;
    private String fromAddress;
    private String fromWardName;
    private String fromDistrictName;
    private String fromProvinceName;

    private String toName;
    private String toPhone;
    private String toAddress;
    private String toWardName;
    private String toDistrictName;
    private String toProvinceName;

    @CreationTimestamp
    private LocalDateTime createdAt; // Thời gian tạo đơn hàng
    @UpdateTimestamp
    private LocalDateTime updatedAt; // Thời gian cập nhật đơn hàng
}
