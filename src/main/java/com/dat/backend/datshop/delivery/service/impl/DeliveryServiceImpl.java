package com.dat.backend.datshop.delivery.service.impl;
import com.dat.backend.datshop.coupon.entity.Coupon;
import com.dat.backend.datshop.config.WebClientConfig;
import com.dat.backend.datshop.delivery.dto.*;
import com.dat.backend.datshop.delivery.entity.Delivery;
import com.dat.backend.datshop.delivery.entity.DeliveryStatus;
import com.dat.backend.datshop.delivery.mapper.DeliveryMapper;
import com.dat.backend.datshop.delivery.repository.DeliveryRepository;
import com.dat.backend.datshop.delivery.service.DeliveryService;
import com.dat.backend.datshop.order.entity.Order;
import com.dat.backend.datshop.order.entity.OrderItem;
import com.dat.backend.datshop.order.entity.OrderStatus;
import com.dat.backend.datshop.order.repository.OrderItemRepository;
import com.dat.backend.datshop.order.repository.OrderRepository;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import com.dat.backend.datshop.util.ConvertStringToLocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final WebClientConfig webClientConfig;
    private final DeliveryMapper deliveryMapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Value("${ghn.shopId}")
    private String shopId;
    @Value("${ghn.token}")
    private String ghn_token;
    @Value("${ghn.apiUrl}")
    private String ghn_url;

    public DeliveryResponse createDelivery(CreateDeliveryForOrder createDeliveryForOrder) {

        // Chỉ cho phép tạo đơn khi đơn hàng trong trạng thái PREPARING
        checkOrderStatus(createDeliveryForOrder.getOrderId());

        // Lấy ra các thông tin cần thiết từ CreateDeliveryForOrder và chuyển đổi sang CreateDeliveryGHNRequest

        CreateDeliveryGHNRequest createDeliveryGHNRequest = createNewDeliveryGHNRequest(createDeliveryForOrder);

        log.info("createDeliveryGHNRequest: {}", createDeliveryGHNRequest);

        CreateDeliveryResponse createDeliveryResponse = webClientConfig.webClient()
                .post()
                .uri(ghn_url+"/shipping-order/create")
                .header("Content-Type", "application/json")
                .header("token", ghn_token)
                .header("shopId", shopId)
                .bodyValue(createDeliveryGHNRequest)
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

        log.info("createDeliveryResponse: {}", createDeliveryResponse);

        // Nhận phản hồi từ GHN
        DataResponse dataResponse = createDeliveryResponse.getData();

        // Cập nhật trạng thái đơn hàng sang SHIPPING
        Order order = orderRepository.findById(createDeliveryForOrder.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + createDeliveryForOrder.getOrderId()));
        order.setOrderStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);

        // Tạo đơn vận chuyển mới
        Delivery delivery = deliveryMapper.createDeliveryRequestToDeliveryEntity(createDeliveryGHNRequest);
        delivery.setDeliveryStatus(DeliveryStatus.PENDING);
        delivery.setGhnOrderCode(dataResponse.getOrder_code());
        delivery.setUserId(createDeliveryForOrder.getUserId());
        delivery.setShopId(createDeliveryForOrder.getShopId());
        delivery.setOrderId(createDeliveryForOrder.getOrderId());
        delivery.setTotalFee((long) dataResponse.getTotal_fee());

        // Chuyển đổi expected_delivery_time từ chuỗi sang LocalDateTime
        LocalDateTime expectedDeliveryTime = ConvertStringToLocalDateTime.convert(dataResponse.getExpected_delivery_time());
        if (expectedDeliveryTime != null) {
            delivery.setExpectedDeliveryTime(expectedDeliveryTime);
        } else {
            log.warn("Thời gian giao hàng dự kiến không hợp lệ: {}", dataResponse.getExpected_delivery_time());
        }

        deliveryRepository.save(delivery);

        return DeliveryResponse.builder()
                .id(delivery.getId())
                .order_code(dataResponse.getOrder_code())
                .total_fee(dataResponse.getTotal_fee())
                .trans_type(dataResponse.getTrans_type())
                .expected_delivery_time(expectedDeliveryTime)
                .build();
    }

    private void checkOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (!order.getOrderStatus().equals(OrderStatus.PREPARING)) {
            throw new RuntimeException("Cannot create delivery for order with status: " + order.getOrderStatus());
        }
        log.info("Order with id {} is in PREPARING status, proceeding to create delivery.", orderId);
    }

    private CreateDeliveryGHNRequest createNewDeliveryGHNRequest(CreateDeliveryForOrder createDeliveryForOrder) {
        Long orderId = createDeliveryForOrder.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        String clientOrderCode = "shop_" + orderId;
        String coupon = Optional.ofNullable(order.getCoupon()).map(Coupon::getCode).orElse(null);
        String requiredNote = order.getRequiredNote().toString();
        String note = order.getNote();

        Long userId = createDeliveryForOrder.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        String fromName = user.getFullName();
        String fromPhone = user.getPhone();
        String fromAddress = user.getAddress();
        String fromWardName = user.getWardName();
        String fromDistrictName = user.getDistrictName();
        String fromProvinceName = user.getProvinceName();

        Long shopId = createDeliveryForOrder.getShopId();
        User shop = userRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " +
                        shopId));
        String toName = shop.getFullName();
        String toPhone = shop.getPhone();
        String toAddress = shop.getAddress();
        String toWardName = shop.getWardName();
        String toDistrictName = shop.getDistrictName();
        String toProvinceName = shop.getProvinceName();

        // Lấy danh sách các sản phẩm trong đơn hàng
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        // Map các sản phẩm sang GhnItem
        List<GhnItem> items = orderItems.stream().map(orderItem -> {
            GhnItem ghnItem = new GhnItem();
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Information not found with id: " + orderItem.getProductId()));
            ghnItem.setName(product.getName());
            ghnItem.setCode("pr_" + product.getId());
            ghnItem.setQuantity(orderItem.getQuantity());
            ghnItem.setPrice(product.getPrice());
            return ghnItem;
        }).collect(Collectors.toList());

        // Tạo đối tượng CreateDeliveryGHNRequest
        CreateDeliveryGHNRequest createDeliveryGHNRequest = new CreateDeliveryGHNRequest();
        createDeliveryGHNRequest.setClient_order_code(clientOrderCode);
        createDeliveryGHNRequest.setCoupon(coupon);
        createDeliveryGHNRequest.setRequired_note(requiredNote);
        createDeliveryGHNRequest.setNote(note);
        createDeliveryGHNRequest.setFrom_name(fromName);
        createDeliveryGHNRequest.setFrom_phone(fromPhone);
        createDeliveryGHNRequest.setFrom_address(fromAddress);
        createDeliveryGHNRequest.setFrom_ward_name(fromWardName);
        createDeliveryGHNRequest.setFrom_district_name(fromDistrictName);
        createDeliveryGHNRequest.setFrom_province_name(fromProvinceName);
        createDeliveryGHNRequest.setTo_name(toName);
        createDeliveryGHNRequest.setTo_phone(toPhone);
        createDeliveryGHNRequest.setTo_address(toAddress);
        createDeliveryGHNRequest.setTo_ward_name(toWardName);
        createDeliveryGHNRequest.setTo_district_name(toDistrictName);
        createDeliveryGHNRequest.setTo_province_name(toProvinceName);
        createDeliveryGHNRequest.setItems(items);

        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info("Sending JSON to GHN: {}", mapper.writeValueAsString(createDeliveryGHNRequest));
        }
        catch (Exception e) {
            log.error("Error converting CreateDeliveryGHNRequest to JSON: {}", e.getMessage());
        }
        return createDeliveryGHNRequest;
    }

    // Hủy đơn giao hàng
    @Transactional
    public List<CancelDataResponse> cancelDelivery(CancelDeliveryRequest cancelDeliveryRequest) {
        // Gọi API GHN để hủy đơn hàng
        CancelResponse cancelResponse = webClientConfig.webClient()
                .post()
                .uri(ghn_url+"/switch-status/cancel")
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

        // Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
        updateOrderAndDeliveryStatus(cancelDeliveryRequest.getOrder_codes());

        return cancelResponse.getData();
    }

    @Override
    public DeliveryResponse viewDeliveryByOrderId(Long orderId) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByOrderId(orderId);
        if (optionalDelivery.isPresent()) {
            Delivery delivery = optionalDelivery.get();
            return DeliveryResponse.builder()
                    .id(delivery.getId())
                    .order_code(delivery.getGhnOrderCode())
                    .total_fee(Math.toIntExact(delivery.getTotalFee()))
                    .expected_delivery_time(delivery.getExpectedDeliveryTime())
                    .build();
        }
        return null;
    }

    private void updateOrderAndDeliveryStatus(List<String> orderCodes) {
        // Cập nhật trạng thái đơn hàng và trạng thái giao hàng trong cơ sở dữ liệu cho từng mã đơn hàng
        for (String orderCode : orderCodes) {
            // Cập nhật trạng thái giao hàng
            Delivery delivery = deliveryRepository.findByGhnOrderCode(orderCode)
                    .orElseThrow(() -> new RuntimeException("Delivery not found with GHN order code: " + orderCode));
            delivery.setDeliveryStatus(DeliveryStatus.CANCEL);
            deliveryRepository.save(delivery);

            // Cập nhật trạng thái đơn hàng
            Order order = orderRepository.findById(delivery.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + delivery.getOrderId()));
            order.setOrderStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        }
    }
}
