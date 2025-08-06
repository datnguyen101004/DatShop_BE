package com.dat.backend.datshop.delivery.controller;

import com.dat.backend.datshop.delivery.dto.*;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/shop/delivery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_SHOP') or hasRole('ROLE_ADMIN')")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/create")
    public ResponseEntity<DeliveryResponse> createDeliveryService(@RequestBody CreateDeliveryForOrder createDeliveryForOrder) {
        return ResponseEntity.ok(deliveryService.createDelivery(createDeliveryForOrder));
    }

    @PostMapping("/cancel")
    public ResponseEntity<List<CancelDataResponse>> cancelDeliveryService(@RequestBody CancelDeliveryRequest cancelDeliveryRequest) {
        return ResponseEntity.ok(deliveryService.cancelDelivery(cancelDeliveryRequest));
    }
}
