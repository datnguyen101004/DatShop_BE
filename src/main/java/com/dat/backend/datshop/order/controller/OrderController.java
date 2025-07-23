package com.dat.backend.datshop.order.controller;

import com.dat.backend.datshop.order.dto.CreateOrderRequest;
import com.dat.backend.datshop.order.dto.OrderResponse;
import com.dat.backend.datshop.order.service.OrderService;
import com.dat.backend.datshop.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<OrderResponse> createNewOrder(@RequestBody CreateOrderRequest createOrderRequest,
                                                     HttpServletRequest request,
                                                     Authentication authentication) {
        return ApiResponse.success(orderService.createNewOrder(createOrderRequest, request, authentication.getName()));
    }

    @GetMapping("/all")
    public ApiResponse<List<OrderResponse>> getAllOrders(Authentication authentication) {
        return ApiResponse.success(orderService.getAllOrders(authentication.getName()));
    }
}
