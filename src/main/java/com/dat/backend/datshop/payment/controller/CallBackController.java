package com.dat.backend.datshop.payment.controller;

import com.dat.backend.datshop.payment.service.PaymentService;
import com.dat.backend.datshop.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/callback")
@RequiredArgsConstructor
public class CallBackController {
    private final PaymentService paymentService;

    @GetMapping("/payment-callback")
    public ApiResponse<String> paymentCallbackHandler(HttpServletRequest request) {
        return ApiResponse.success(paymentService.paymentCallbackHandler(request));
    }
}
