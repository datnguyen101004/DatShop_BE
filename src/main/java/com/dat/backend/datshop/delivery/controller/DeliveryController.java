package com.dat.backend.datshop.delivery.controller;

import com.dat.backend.datshop.delivery.dto.CancelDataResponse;
import com.dat.backend.datshop.delivery.dto.CancelDeliveryRequest;
import com.dat.backend.datshop.delivery.dto.CreateDeliveryRequest;
import com.dat.backend.datshop.delivery.dto.DeliveryResponse;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import com.dat.backend.datshop.template.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.Role;
import java.util.List;

@RestController
@RequestMapping("api/v1/shop/delivery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_SHOP') or hasRole('ROLE_ADMIN')")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/create")
    public ResponseEntity<DeliveryResponse> createDeliveryService(@RequestBody CreateDeliveryRequest createDeliveryRequest) throws JsonProcessingException {
        return ResponseEntity.ok(deliveryService.createDelivery(createDeliveryRequest));
    }

    @PostMapping("/cancel")
    public ResponseEntity<List<CancelDataResponse>> cancelDeliveryService(@RequestBody CancelDeliveryRequest cancelDeliveryRequest) {
        return ResponseEntity.ok(deliveryService.cancelDelivery(cancelDeliveryRequest));
    }
}
