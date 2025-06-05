package com.dat.backend.datshop.production.mapper;

import com.dat.backend.datshop.production.dto.CreateProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;
import com.dat.backend.datshop.production.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "author", expression = "java(product.getAuthor().getName())")
    ProductResponse productToProductResponse(Product product);
    @Mapping(target = "id", ignore = true)
    Product createProductToProduct(CreateProduct createProduct);
}
