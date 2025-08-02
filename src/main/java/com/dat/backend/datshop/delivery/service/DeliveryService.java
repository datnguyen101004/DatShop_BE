package com.dat.backend.datshop.delivery.service;

import com.dat.backend.datshop.delivery.dto.CancelDataResponse;
import com.dat.backend.datshop.delivery.dto.CancelDeliveryRequest;
import com.dat.backend.datshop.delivery.dto.CreateDeliveryRequest;
import com.dat.backend.datshop.delivery.dto.DeliveryResponse;

import java.util.List;

public interface DeliveryService {

    DeliveryResponse createDelivery(CreateDeliveryRequest createDeliveryRequest);

    List<CancelDataResponse> cancelDelivery(CancelDeliveryRequest cancelDeliveryRequest);
}
