package com.dat.backend.datshop.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelDataResponse {
    private String order_code;
    private String result;
    private String message;
}
