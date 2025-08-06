package com.dat.backend.datshop.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private Long id;
    private String order_code;
    private String trans_type;
    private int total_fee;
    private LocalDateTime expected_delivery_time;
}
