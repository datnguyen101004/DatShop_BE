package com.dat.backend.datshop.payment.controller;

import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import com.dat.backend.datshop.payment.service.PaymentService;
import com.dat.backend.datshop.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/payment/vnpay")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-payment-url")
    public ApiResponse<BillResponse> createPaymentUrl(@RequestBody PayRequest payRequest,
                                                      HttpServletRequest request,
                                                      Authentication authentication) {
        return ApiResponse.success(paymentService.createPayment(payRequest, request, authentication.getName()));
    }

    @GetMapping("/all")
    public ApiResponse<List<BillResponse>> getAllBills(Authentication authentication) {
        return ApiResponse.success(paymentService.getAllBills(authentication.getName()));
    }
}
