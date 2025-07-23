package com.dat.backend.datshop.order.service;

import com.dat.backend.datshop.order.dto.CreateOrderRequest;
import com.dat.backend.datshop.order.dto.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createNewOrder(CreateOrderRequest createOrderRequest, HttpServletRequest request, String name);

    List<OrderResponse> getAllOrders(String name);

    String paymentCallbackHandler(HttpServletRequest request);
}
