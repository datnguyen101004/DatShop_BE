package com.dat.backend.datshop.delivery.entity;

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
@Entity(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId; // ID của người nhận
    private Long shopId; // ID của shop
    private String clientOrderCode; // Mã đơn hàng của khách hàng
    private String ghnOrderCode; // Mã đơn hàng của GHN
    private Long totalFee; // Tổng phí giao hàng
    private LocalDateTime expectedDeliveryTime; // Thời gian giao hàng dự kiến
    private String note; // Ghi chú của người dùng về đơn hàng

    @Enumerated(EnumType.STRING)
    private RequiredNoteGHN required_note; // Ghi chú bắt buộc, Bao gồm: CHOTHUHANG, CHOXEMHANGKHONGTHU, KHONGCHOXEMHANG
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus; // Trạng thái đơn hàng (ví dụ: PENDING (đang xử lý), SUCCESS (đã giao hàng), CANCEL (đã hủy), v.v.)

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
