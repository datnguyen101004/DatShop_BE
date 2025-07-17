package com.dat.backend.datshop.payment.service;

import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PaymentService {
    BillResponse createPayment(PayRequest payRequest, HttpServletRequest request, String email);

    String paymentCallbackHandler(HttpServletRequest request);

    List<BillResponse> getAllBills(String email);
}
