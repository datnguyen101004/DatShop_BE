package com.dat.backend.datshop.delivery.service.impl;

import com.dat.backend.datshop.delivery.config.WebClientConfig;
import com.dat.backend.datshop.delivery.dto.*;
import com.dat.backend.datshop.delivery.entity.Delivery;
import com.dat.backend.datshop.delivery.entity.DeliveryStatus;
import com.dat.backend.datshop.delivery.mapper.DeliveryMapper;
import com.dat.backend.datshop.delivery.repository.DeliveryRepository;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final WebClientConfig webClientConfig;
    private final DeliveryMapper deliveryMapper;

    @Value("${ghn.shopId}")
    private String shopId;
    @Value("${ghn.token}")
    private String ghn_token;

    public DeliveryResponse createDelivery(CreateDeliveryRequest createDeliveryRequest) {
        CreateDeliveryResponse createDeliveryResponse = webClientConfig.webClient()
                .post()
                .uri("/shipping-order/create")
                .header("Content-Type", "application/json")
                .header("token", ghn_token)
                .header("shopId", shopId)
                .bodyValue(createDeliveryRequest)
                .exchangeToMono(response -> {
                    if (response.statusCode().is4xxClientError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("GHN 4xx error: {}", errorBody);
                                    return Mono.error(new RuntimeException("GHN Error: " + errorBody));
                                });
                    }
                    return response.bodyToMono(CreateDeliveryResponse.class);
                })
                .block();

        if (createDeliveryResponse == null) {
            log.error("Failed to create delivery: response is null");
            return null;
        }

        // Nhận phản hồi từ GHN
        DataResponse dataResponse = createDeliveryResponse.getData();

        // Tạo đơn vận chuyển mới
        Delivery delivery = deliveryMapper.createDeliveryRequestToDeliveryEntity(createDeliveryRequest);
        delivery.setDeliveryStatus(DeliveryStatus.PENDING);
        delivery.setGhnOrderCode(dataResponse.getOrder_code());
        delivery.setTotalFee((long) dataResponse.getTotal_fee());

        deliveryRepository.save(delivery);

        return DeliveryResponse.builder()
                .id(delivery.getId())
                .order_code(dataResponse.getOrder_code())
                .total_fee(dataResponse.getTotal_fee())
                .trans_type(dataResponse.getTrans_type())
                .expected_delivery_time(dataResponse.getExpected_delivery_time())
                .build();
    }

    // Hủy đơn giao hàng
    public List<CancelDataResponse> cancelDelivery(CancelDeliveryRequest cancelDeliveryRequest) {
        // Gọi API GHN để hủy đơn hàng
        CancelResponse cancelResponse = webClientConfig.webClient()
                .post()
                .uri("/switch-status/cancel")
                .header("Content-Type", "application/json")
                .header("token", ghn_token)
                .header("shopId", shopId)
                .bodyValue(cancelDeliveryRequest)
                .retrieve()
                .bodyToMono(CancelResponse.class)
                .block();

        if (cancelResponse == null ||cancelResponse.getCode() != 200) {
            throw new RuntimeException("Not found order");
        }
        return cancelResponse.getData();
    }
}
