package com.dat.backend.datshop.product.mapper;

import com.dat.backend.datshop.product.dto.ActionToProduct;
import com.dat.backend.datshop.product.dto.ProductResponse;
import com.dat.backend.datshop.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse productToProductResponse(Product product);
    @Mapping(target = "id", ignore = true)
    Product createProductToProduct(ActionToProduct actionToProduct);
}
