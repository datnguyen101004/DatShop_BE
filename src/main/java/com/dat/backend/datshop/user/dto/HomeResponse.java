package com.dat.backend.datshop.user.dto;

import com.dat.backend.datshop.product.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponse {
    private List<String> categories;
    private List<ProductResponse> products;
}
