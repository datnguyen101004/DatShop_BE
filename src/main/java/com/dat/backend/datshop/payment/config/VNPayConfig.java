package com.dat.backend.datshop.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VNPayConfig {

    @Value("${vnpay.vnp_PayUrl}")
    public String vnp_PayUrl;
    @Value("${vnpay.vnp_TmnCode}")
    public String vnp_TmnCode;
    @Value("${vnpay.vnp_HashSecret}")
    public String vnp_HashSecret;
    @Value("${vnpay.vnp_ReturnUrl}")
    public String vnp_returnUrl;
    @Value("${vnpay.vnp_ApiUrl}")
    public String vnp_apiUrl;

    @Bean
    public Map<String, String> createVnPayUrl() {
        Map< String, String > vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", "A" + System.currentTimeMillis());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang mua hang tai cua hang Dat Shop");
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_returnUrl);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(formatter));
        vnp_Params.put("vnp_ExpireDate", LocalDateTime.now().plusDays(1).format(formatter));

        return vnp_Params;
    }

    public Map<String, String> getPaymentReturnParams(String vnp_BankTranNo, String vnp_ResponseCode, String vnp_TransactionNo) {
        Map< String, String > vnp_CallbackParams = new HashMap<>();
        vnp_CallbackParams.put("vnp_BankTranNo", vnp_BankTranNo);
        vnp_CallbackParams.put("vnp_ResponseCode", vnp_ResponseCode);
        vnp_CallbackParams.put("vnp_TransactionNo", vnp_TransactionNo);
        return vnp_CallbackParams;
    }
}
