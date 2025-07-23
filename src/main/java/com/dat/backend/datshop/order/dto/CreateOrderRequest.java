package com.dat.backend.datshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private String note;
    private Long couponId;
    private String paymentMethod;
    private List<ProductItem> productItems; // Danh sách sản phẩm trong đơn hàng
    private String requiredNote; // Ghi chú về yêu cầu giao hàng (ví dụ: không cho xem hàng, cho xem hàng, v.v.)
}
