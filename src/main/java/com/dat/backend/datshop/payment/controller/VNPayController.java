package com.dat.backend.datshop.payment.controller;

import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import com.dat.backend.datshop.payment.service.VNPayService;
import com.dat.backend.datshop.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnpayService;

    @PostMapping("/create-payment-url")
    public ApiResponse<BillResponse> createPaymentUrl(@RequestBody PayRequest payRequest,
                                                      HttpServletRequest request,
                                                      Authentication authentication) {
        return ApiResponse.success(vnpayService.createPayment(payRequest, request, authentication.getName()));
    }

    @GetMapping("/payment-callback")
    public ApiResponse<String> paymentCallbackHandler(HttpServletRequest request) {
        return ApiResponse.success(vnpayService.paymentCallbackHandler(request));
    }
}
