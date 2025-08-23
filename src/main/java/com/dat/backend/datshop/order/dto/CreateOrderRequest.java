package com.dat.backend.datshop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Phương thức thanh toán: COD, BANK_TRANSFER, CREDIT_CARD, ...",
            example = "COD"
    )
    private String paymentMethod;
    private List<ProductItem> productItems; // Danh sách sản phẩm trong đơn hàng

    @Schema(
            description = "Ghi chú về yêu cầu giao hàng: CHOTHUHANG, CHOXEMHANGKHONGTHU, KHONGCHOXEMHANG",
            example = "CHOTHUHANG"
    )
    private String requiredNote; // Ghi chú về yêu cầu giao hàng (ví dụ: không cho xem hàng, cho xem hàng, v.v.)
}
