package com.dat.backend.datshop.payment.controller;

import com.dat.backend.datshop.payment.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnpayService;

    @GetMapping("/create-payment-url")
    public String createPaymentUrl(@RequestParam Long amount, HttpServletRequest request) {
        return vnpayService.createPayment(amount, request);
    }

    @GetMapping("/payment-callback")
    public String paymentCallbackHandler(HttpServletRequest request) {
        return vnpayService.paymentCallbackHandler(request);
    }
}
