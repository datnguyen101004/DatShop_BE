package com.dat.backend.datshop.production.mapper;

import com.dat.backend.datshop.production.dto.CreateOrEditProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;
import com.dat.backend.datshop.production.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse productToProductResponse(Product product);
    @Mapping(target = "id", ignore = true)
    Product createProductToProduct(CreateOrEditProduct createOrEditProduct);
}
