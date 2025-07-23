package com.dat.backend.datshop.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GhnItem {
    private String name;
    @Schema(description = "Mã của sản phầm. Lấy ra Id của sản phầm trong db")
    private String code;
    private int quantity;
    private int price;
}
