package com.dat.backend.datshop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionToProduct {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int stockQuantity;
    private String category;
}
