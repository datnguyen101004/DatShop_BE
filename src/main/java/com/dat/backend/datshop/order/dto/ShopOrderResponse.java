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
public class ShopOrderResponse {
    private Long orderId;
    private Long userId;
    private Long shopId;
    private String orderStatus;
    private List<ProductItem> productItems;
    private Long totalPrice; // Tổng giá trị của đơn hàng
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
