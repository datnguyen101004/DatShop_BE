package com.dat.backend.datshop.payment.service.impl;

import com.dat.backend.datshop.payment.config.VNPayConfig;
import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import com.dat.backend.datshop.payment.service.VNPayService;
import com.dat.backend.datshop.payment.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {
    private final VNPayConfig vnPayConfig;

    @Transactional
    public BillResponse createPayment(PayRequest payRequest, HttpServletRequest request, String email) {
        // Fetch amount from PayRequest

        String ipAddr = VNPayUtil.getIpAddr(request);
        Map<String, String> vnpParamsMap = vnPayConfig.createVnPayUrl();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount*100L));
        vnpParamsMap.put("vnp_IpAddr", ipAddr);
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.vnp_HashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        return vnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public String paymentCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return "Success";
        } else {
            return "Failed";
        }
    }
}
