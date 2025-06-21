package com.dat.backend.datshop.payment.service;

import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface VNPayService {
    BillResponse createPayment(List<PayRequest> payRequestList, HttpServletRequest request, String email);

    String paymentCallbackHandler(HttpServletRequest request);
}
