package com.dat.backend.datshop.order.service.impl;

import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.coupon.entity.Coupon;
import com.dat.backend.datshop.coupon.entity.CouponType;
import com.dat.backend.datshop.coupon.repository.CouponRepository;
import com.dat.backend.datshop.order.config.VNPayConfig;
import com.dat.backend.datshop.order.dto.CreateOrderRequest;
import com.dat.backend.datshop.order.dto.OrderResponse;
import com.dat.backend.datshop.order.dto.ProductItem;
import com.dat.backend.datshop.order.entity.*;
import com.dat.backend.datshop.order.mapper.OrderMapper;
import com.dat.backend.datshop.order.repository.BillRepository;
import com.dat.backend.datshop.order.repository.OrderItemRepository;
import com.dat.backend.datshop.order.repository.OrderRepository;
import com.dat.backend.datshop.order.service.OrderService;
import com.dat.backend.datshop.order.util.VNPayUtil;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final VNPayConfig vnPayConfig;
    private final BillRepository billRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderResponse createNewOrder(CreateOrderRequest createOrderRequest,
                                        HttpServletRequest request,
                                        String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo đơn hàng mới
        // Chuyển đổi từ DTO sang Entity
        Order newOrder = orderMapper.toOrderEntity(createOrderRequest);
        newOrder.setUserId(user.getId());
        newOrder.setOrderStatus(OrderStatus.PENDING); // Mặc định trạng thái đơn hàng là PENDING
        newOrder.setCreatedAt(LocalDateTime.now());

        // Chuyển coupon từ id sang entity nếu có
        Long couponId = createOrderRequest.getCouponId();
        // Tính toán tổng giá trị đơn hàng
        long totalPrice = createOrderRequest.getProductItems().stream()
                .mapToLong(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));
                    return (long) (product.getPrice() * item.getQuantity());
                })
                .sum();

        // Kiểm tra xem có coupon không
        if (couponId != null) {
            Optional<Coupon> couponOp = couponRepository.findById(couponId);

            // Kiểm tra xem coupon có tồn tại không
            if (couponOp.isPresent()) {
                // Kiểm tra xem coupon có hơp lệ không
                if (!couponOp.get().getIsActive() || couponOp.get().getQuantity() <= 0) {
                    throw new RuntimeException("Coupon is not valid");
                }

                // Nếu coupon hợp lệ, gán vào đơn hàng
                Coupon coupon = couponOp.get();
                newOrder.setCoupon(coupon);

                // Tính toán tổng giá trị đơn hàng sau khi áp dụng coupon
                if (coupon.getCouponType() == CouponType.PERCENT) {
                    // Giảm giá theo phần trăm
                    totalPrice = (long) (totalPrice - (totalPrice * coupon.getDiscountAmount() / 100));
                } else if (coupon.getCouponType() == CouponType.MONEY) {
                    // Giảm giá theo giá trị cố định
                    totalPrice = (long) (totalPrice - coupon.getDiscountAmount());
                }
            } else {
                log.warn("Coupon with id {} not found", couponId);
            }
        }

        // Gán tổng giá trị đơn hàng
        newOrder.setTotalPrice(totalPrice);

        // Lưu đơn hàng mới vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(newOrder);

        // Lưu các sản phẩm trong đơn hàng
        for (ProductItem item : createOrderRequest.getProductItems()) {
            OrderItem orderItem = orderMapper.toOrderItemEntity(item);
            orderItem.setOrderId(savedOrder.getId());
            orderItemRepository.save(orderItem);
        }

        // Kiểm tra xem nếu đơn hàng là chuyển khoản thì tạo payment url
        if (createOrderRequest.getPaymentMethod().equalsIgnoreCase(PaymentMethod.BANK_TRANSFER.name())) {

            // Tạo payment url
            String paymentUrl = createPayment(totalPrice, savedOrder.getId(), request);
            Bill bill = new Bill();
            bill.setId(UUID.randomUUID().toString());
            bill.setOrderId(savedOrder.getId());
            bill.setPaymentStatus(PaymentStatus.PENDING);
            bill.setPaymentUrl(paymentUrl);
            billRepository.save(bill);

            // Cập nhật trạng thái đơn hàng là WAITING_FOR_PAYMENT
            savedOrder.setOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);
            orderRepository.save(savedOrder);

            // Trả về thông tin đơn hàng kèm theo paymentUrl
            OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);
            orderResponse.setProductItems(createOrderRequest.getProductItems());
            orderResponse.setPaymentUrl(paymentUrl);
            return orderResponse;
        } else {
            // Nếu không phải chuyển khoản, lưu trạng thái là PREPARING
            savedOrder.setOrderStatus(OrderStatus.PREPARING);

            // Giảm số lượng coupon nếu có
            if (savedOrder.getCoupon() != null) {
                applyCouponUsage(savedOrder.getCoupon());
            }
        }

        // Chuyển đổi sang DTO để trả về
        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);
        orderResponse.setProductItems(createOrderRequest.getProductItems());
        return orderResponse;
    }

    // Tạo URL thanh toán VNPay
    private String createPayment(Long totalPrice, Long orderId, HttpServletRequest request) {
        // Convert totalPrice sang đơn vị đồng của VNPay (1 VNĐ = 100 đồng)
        totalPrice *= 100;
        log.info("Creating payment for order id: {}, total price: {}", orderId, totalPrice);

        // Tạo URL thanh toán VNPay
        String ipAddr = VNPayUtil.getIpAddr(request);
        Map<String, String> vnpParamsMap = vnPayConfig.createVnPayUrl(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(totalPrice));
        vnpParamsMap.put("vnp_IpAddr", ipAddr);
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.vnp_HashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;
        log.info("Payment URL created: {}", paymentUrl);
        return paymentUrl;
    }

    // Giảm số lượng coupon khi sử dụng
    private void applyCouponUsage(Coupon coupon) {
        // Giảm số lượng coupon
        coupon.setQuantity(coupon.getQuantity() - 1);
        if (coupon.getQuantity() <= 0) {
            // Nếu số lượng coupon giảm xuống 0, đánh dấu là không còn hiệu lực
            coupon.setIsActive(false);
        }
        couponRepository.save(coupon);
    }

    @Override
    public List<OrderResponse> getAllOrders(String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findAllByUserId(user.getId());
        List<OrderResponse> orderResponses = new ArrayList<>();

        // Kiểm tra xem có đơn hàng nào không
        if (!orders.isEmpty()) {
            // Chuyển đổi danh sách đơn hàng sang danh sách DTO
            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
                List<ProductItem> productItems = orderItems.stream().map(orderMapper::toProductItemDto).toList();
                OrderResponse orderResponse = orderMapper.toOrderResponse(order);
                orderResponse.setProductItems(productItems);
                orderResponses.add(orderResponse);
            }
            return orderResponses;
        }

        return List.of();
    }

    @Override
    @Transactional
    public String paymentCallbackHandler(HttpServletRequest request) {
        // Lấy các tham số từ request
        String status = request.getParameter("vnp_ResponseCode");
        String orderId = request.getParameter("orderId");

        // Kiểm tra trạng thái thanh toán
        if (status.equals("00")) {
            /*                       PaymentMethod successful
             * Cập nhật trạng thái đơn hàng và bill
             */

            // Lấy đơn hàng và bill
            Order order = orderRepository.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            Bill bill = billRepository.findByOrderId(Long.valueOf(orderId))
                    .orElseThrow(() -> new RuntimeException("Bill not found for order id: " + orderId));

            // Cập nhật trạng thái đơn hàng
            order.setOrderStatus(OrderStatus.PREPARING);
            bill.setPaymentStatus(PaymentStatus.SUCCESS);

            // Lưu cập nhật đơn hàng và bill
            orderRepository.save(order);
            billRepository.save(bill);

            // Giảm số lượng coupon nếu có
            if (order.getCoupon() != null) {
                applyCouponUsage(order.getCoupon());
            }

            // Giảm số lượng sản phẩm trong kho
            // Lấy tất cả các sản phẩm trong đơn hàng
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.save(product);
            }

            // Giảm số lượng sản phẩm trong giỏ hàng
            reduceCartItems(order.getUserId(), orderItems);

            log.info("Payment successful for order id: {}", orderId);
            return "SUCCESS";
        }

        // Trường hợp thanh toán không thành công
        log.warn("Payment failed for order id: {} with status: {}", orderId, status);

        return "FAILED";
    }

    // Giảm số lượng sản phẩm trong giỏ hàng
    private void reduceCartItems(Long userId, List<OrderItem> orderItems) {
        // Lấy giỏ hàng của người dùng
        Optional<Cart> cartOptional = cartRepository.findById(userId);

        // Nếu giỏ hàng không tồn tại, không cần làm gì
        if (cartOptional.isEmpty()) {
            return;
        }

        Cart cart = cartOptional.get();

        // Lấy danh sách các sản phẩm trong giỏ hàng trùng với sản phẩm trong đơn hàng
        for (OrderItem orderItem : orderItems) {
            Optional<CartItem> cartItems = cartItemRepository.findByCartIdAndProductId(cart.getId(), orderItem.getProductId());

            // Nếu sản phẩm tồn tại trong giỏ hàng giảm số lượng
            if (cartItems.isPresent()) {

                CartItem cartItem = cartItems.get();

                // Giảm số lượng sản phẩm trong giỏ hàng
                int newQuantity = cartItem.getQuantity() - orderItem.getQuantity();

                // Nếu số lượng giảm xuống 0 hoặc âm, xóa sản phẩm khỏi giỏ hàng
                if (newQuantity <= 0) {
                    cartItemRepository.delete(cartItem);
                } else {
                    // Cập nhật số lượng sản phẩm trong giỏ hàng
                    cartItem.setQuantity(newQuantity);
                    cartItemRepository.save(cartItem);
                }
            }
            // Nếu sản phẩm không tồn tại trong giỏ hàng, không cần làm gì
        }
    }
}
