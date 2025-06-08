package com.dat.backend.datshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddOrRemoveProduct {
    private Long productId;
    private int quantity;
}
