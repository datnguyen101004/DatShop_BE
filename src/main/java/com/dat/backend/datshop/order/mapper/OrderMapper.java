package com.dat.backend.datshop.order.mapper;

import com.dat.backend.datshop.order.dto.CreateOrderRequest;
import com.dat.backend.datshop.order.dto.OrderResponse;
import com.dat.backend.datshop.order.dto.ProductItem;
import com.dat.backend.datshop.order.dto.ShopOrderResponse;
import com.dat.backend.datshop.order.entity.Order;
import com.dat.backend.datshop.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrderEntity(CreateOrderRequest createOrderRequest);

    OrderItem toOrderItemEntity(ProductItem productItem);

    ProductItem toProductItemDto(OrderItem orderItem);

    @Mapping(target = "orderId", source = "id")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "shopId", expression = "java(order.getShop().getId())")
    @Mapping(target = "orderId", source = "id")
    ShopOrderResponse toShopOrderResponse(Order order);
}
