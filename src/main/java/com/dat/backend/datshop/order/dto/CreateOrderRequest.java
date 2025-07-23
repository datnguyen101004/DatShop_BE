package com.dat.backend.datshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrder {
    private List<String> couponCodes;
    private List<ProductItem> productItems;
}
