package com.dat.backend.datshop.payment.service;

import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    BillResponse createPayment(PayRequest payRequest, HttpServletRequest request, String email);

    String paymentCallbackHandler(HttpServletRequest request);
}
