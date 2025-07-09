package com.dat.backend.datshop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private String billId;
    private String paymentUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
