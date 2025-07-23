package com.dat.backend.datshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String note;
    private String orderStatus;
    private String paymentMethod;
    // Địa chỉ giao hàng chi tiết
    private String address; // Địa chỉ giao hàng cụ thể
    private String wardName; // Tên phường/xã
    private String districtName; // Tên quận/huyện
    private String provinceName; // Tên tỉnh/thành phố
    private String requiredNote; // Ghi chú về yêu cầu giao hàng (ví dụ: không cho xem hàng, cho xem hàng, v.v.)
    private List<ProductItem> productItems;
    private Long couponId; // ID của coupon được áp dụng cho đơn hàng
    private Long totalPrice; // Tổng giá trị của đơn hàng
    private String paymentUrl; // URL thanh toán nếu có
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
