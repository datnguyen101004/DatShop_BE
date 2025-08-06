package com.dat.backend.datshop.delivery.service;

import com.dat.backend.datshop.delivery.dto.*;

import java.util.List;

public interface DeliveryService {

    DeliveryResponse createDelivery(CreateDeliveryForOrder createDeliveryForOrder);

    List<CancelDataResponse> cancelDelivery(CancelDeliveryRequest cancelDeliveryRequest);
}
