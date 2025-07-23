package com.dat.backend.datshop.order.controller;

import com.dat.backend.datshop.order.service.OrderService;
import com.dat.backend.datshop.template.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/callback")
@RequiredArgsConstructor
public class CallBack {
    private final OrderService orderService;

    @GetMapping("/payment-callback")
    public ApiResponse<String> paymentCallbackHandler(HttpServletRequest request) {
        return ApiResponse.success(orderService.paymentCallbackHandler(request));
    }
}
