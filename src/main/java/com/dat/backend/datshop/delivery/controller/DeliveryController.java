package com.dat.backend.datshop.delivery.controller;

import com.dat.backend.datshop.delivery.dto.CreateDelivery;
import com.dat.backend.datshop.delivery.dto.DeliveryResponse;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import com.dat.backend.datshop.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/shop/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Operation(
            summary = "Tạo một dịch vụ giao hàng mới",
            description = "Tạo một dịch vụ giao hàng mới cho cửa hàng. Chỉ chủ shop mới có quyền tạo dịch vụ giao hàng."
    )
    @PostMapping("/create")
    public ApiResponse<DeliveryResponse> createDeliveryService(@RequestBody CreateDelivery createDelivery, Authentication authentication) {
        return ApiResponse.success(deliveryService.createDeliveryService(createDelivery, authentication.getName()));
    }
}
