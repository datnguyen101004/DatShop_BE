package com.dat.backend.datshop.payment.service;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    String createPayment(Long amount, HttpServletRequest request);

    String paymentCallbackHandler(HttpServletRequest request);
}
