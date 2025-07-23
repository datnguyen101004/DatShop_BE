package com.dat.backend.datshop.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private Long deliveryId;
    private String order_code;
    private String trans_type;
    private FeeResponse fee;
    private int total_fee;
    private String expected_delivery_time;
}
