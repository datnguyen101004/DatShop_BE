package com.dat.backend.datshop.delivery.service;

import com.dat.backend.datshop.delivery.dto.CreateDelivery;
import com.dat.backend.datshop.delivery.dto.DeliveryResponse;

public interface DeliveryService {
    DeliveryResponse createDeliveryService(CreateDelivery createDelivery, String name);
}
