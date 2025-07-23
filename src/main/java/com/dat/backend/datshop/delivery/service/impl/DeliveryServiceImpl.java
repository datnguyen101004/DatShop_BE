package com.dat.backend.datshop.delivery.service.impl;

import com.dat.backend.datshop.delivery.config.WebClientConfig;
import com.dat.backend.datshop.delivery.dto.CreateDelivery;
import com.dat.backend.datshop.delivery.dto.DeliveryResponse;
import com.dat.backend.datshop.delivery.repository.DeliveryRepository;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final WebClientConfig webClientConfig;
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Value("${ghn.token}")
    private String ghnToken;
    @Value("${ghn.shopId}")
    private String ghnShopId;
    @Value("${ghn.apiUrl}")
    private String ghnApiUrl;


    @Override
    public DeliveryResponse createDeliveryService(CreateDelivery createDelivery, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // --------------Tạo mới đơn giao hàng----------------

        // Lấy thông tin từ request



        return null;
    }
}
