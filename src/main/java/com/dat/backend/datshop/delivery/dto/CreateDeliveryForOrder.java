package com.dat.backend.datshop.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryForOrder {
    private Long orderId;
    private Long shopId;
    private Long userId;
    private String note;
}
