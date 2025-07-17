package com.dat.backend.datshop.payment.service.impl;

import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.coupon.entity.Coupon;
import com.dat.backend.datshop.coupon.entity.CouponType;
import com.dat.backend.datshop.coupon.repository.CouponRepository;
import com.dat.backend.datshop.payment.config.VNPayConfig;
import com.dat.backend.datshop.payment.dto.BillItemResponse;
import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import com.dat.backend.datshop.payment.dto.ProductRequest;
import com.dat.backend.datshop.payment.entity.Bill;
import com.dat.backend.datshop.payment.entity.BillCoupons;
import com.dat.backend.datshop.payment.entity.BillStatus;
import com.dat.backend.datshop.payment.entity.BillItems;
import com.dat.backend.datshop.payment.repository.BillCouponsRepository;
import com.dat.backend.datshop.payment.repository.BillRepository;
import com.dat.backend.datshop.payment.repository.Bill_ItemsRepository;
import com.dat.backend.datshop.payment.service.PaymentService;
import com.dat.backend.datshop.payment.util.VNPayUtil;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final VNPayConfig vnPayConfig;
    private final UserRepository userRepository;
    private final BillRepository billRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final Bill_ItemsRepository billItemsRepository;
    private final CouponRepository couponRepository;
    private final BillCouponsRepository billCouponsRepository;

    // Tạo url thanh toán bằng VNPay và lưu thông tin hóa đơn vào cơ sở dữ liệu
    @Override
    @Transactional
    public BillResponse createPayment(PayRequest payRequest, HttpServletRequest request, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy danh sách sản phẩm từ yêu cầu thanh toán
        List<ProductRequest> productRequests = payRequest.getProductRequests();

        if (productRequests == null || productRequests.isEmpty()) {
            throw new RuntimeException("No products found in the payment request");
        }

        // Tính tổng số tiền cần thanh toán
        double amount = productRequests.stream()
                .mapToLong(productRequest -> {
                    Product product = productRepository.findById(productRequest.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productRequest.getProductId()));
                    return (long) (product.getPrice() * productRequest.getQuantity());
                })
                .sum();

        log.info("Total amount to be paid: {}", amount);

        // Tạo hóa đơn mới
        Bill bill = Bill.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .price(amount)
                .status(BillStatus.PENDING)
                .description("Waiting for payment")
                .build();

        // Kiểm tra và áp dụng mã giảm giá nếu có
        List<String> couponCodes = payRequest.getCouponCodes();
        if (couponCodes != null && !couponCodes.isEmpty()) {
            double percent = 0.0;
            double money = 0;
            for (String couponCode : couponCodes) {
                // Lấy mã giảm giá từ cơ sở dữ liệu
                Coupon coupon = couponRepository.findByCode(couponCode)
                        .orElseThrow(() -> new RuntimeException("Coupon not found with code: " + couponCode));

                // Kiểm tra xem mã giảm giá có hợp lệ không
                if (!coupon.getIsActive() || coupon.getExpirationDate().isBefore(java.time.LocalDateTime.now()) || coupon.getQuantity() <= 0) {
                    throw new RuntimeException("Coupon is not active: " + couponCode);
                }

                // Kiểm tra loại mã giảm giá
                if (coupon.getCouponType() == CouponType.PERCENT) {
                    // Mã giảm giá theo phần trăm
                    percent += coupon.getDiscountAmount();
                } else if (coupon.getCouponType() == CouponType.MONEY) {
                    // Mã giảm giá theo số tiền
                    money += coupon.getDiscountAmount();
                }

                // Lưu mã giảm giá vào hóa đơn
                BillCoupons billCoupon = BillCoupons.builder()
                        .billId(bill.getId())
                        .couponId(coupon.getId())
                        .build();

                billCouponsRepository.save(billCoupon);
            }

            // Đảm bảo rằng money không vượt quá 50% của tổng số tiền và percent không vượt quá 50%
            if (money > amount * 0.5) {
                throw new RuntimeException("Discount amount cannot exceed 50% of the total amount");
            }
            if (percent > 50) {
                throw new RuntimeException("Discount percentage cannot exceed 50%");
            }

            // Tính tổng số tiền sau khi áp dụng mã giảm giá
            amount = (amount - money) * (1 - percent / 100);

            // Cập nhật tổng số tiền trong hóa đơn
            bill.setPrice(amount);

            log.info("Total amount after applying coupons: {}", amount);
        }

        // Kiểm tra kho hàng và thêm sản phẩm vào hóa đơn
        for (ProductRequest productRequest : productRequests) {
            Product product = productRepository.findById(productRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productRequest.getProductId()));
            if (product.getStockQuantity() < productRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product ID: " + productRequest.getProductId());
            }

            // Them sản phẩm vào hóa đơn
            BillItems billItem = BillItems.builder()
                    .billId(bill.getId())
                    .productId(productRequest.getProductId())
                    .quantity(productRequest.getQuantity())
                    .build();

            billItemsRepository.save(billItem);
        }

        // Convert amount to long for VNPay
        long totalAmount = (long) (amount * 100); // Convert to VND

        log.info("Total amount to be paid: {}", totalAmount);

        // Create payment URL
        String ipAddr = VNPayUtil.getIpAddr(request);
        Map<String, String> vnpParamsMap = vnPayConfig.createVnPayUrl(bill.getId());
        vnpParamsMap.put("vnp_Amount", String.valueOf(totalAmount));
        vnpParamsMap.put("vnp_IpAddr", ipAddr);
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.vnp_HashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Lưu hóa đơn vào cơ sở dữ liệu
        bill.setPaymentUrl(paymentUrl);
        billRepository.save(bill);

        // Trả về thông tin hóa đơn
        return BillResponse.builder()
                .billId(bill.getId())
                .paymentUrl(paymentUrl)
                .status(bill.getStatus().toString())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }

    @Transactional
    @Override
    public String paymentCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String billId = request.getParameter("billId");
        if (status.equals("00")) {
            /*             * Payment successful
             * Cập nhật trạng thái hóa đơn và xử lý các sản phẩm trong hóa đơn, giỏ hàng
             */

            // Lấy hóa đơn từ cơ sở dữ liệu
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));

            log.info("Bill found: {}", bill);

            // Nếu hóa đơn đã được xử lý thành công, không cần xử lý lại
            if (bill.getStatus() == BillStatus.SUCCESS) {
                return "Already processed";
            }

            // Lây danh sách sản phẩm trong hóa đơn
            List<BillItems> billItems = billItemsRepository.findByBillId(billId);
            
            // Giảm số lượng sản phẩm trong kho
            for (BillItems billItem : billItems) {
                Product product = productRepository.findById(billItem.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + billItem.getProductId()));
                
                // Giảm số lượng sản phẩm trong kho
                if (product.getStockQuantity() < billItem.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product ID: " + billItem.getProductId());
                }
                product.setStockQuantity(product.getStockQuantity() - billItem.getQuantity());
                productRepository.save(product);
            }
            
            // Giảm số lượng sản phẩm trong giỏ hàng
            Optional<Cart> optionalCart = cartRepository.findByUserId(bill.getUserId());
            if (optionalCart.isPresent()) {
                Cart cart = optionalCart.get();
                for (BillItems billItem : billItems) {
                    // Tìm sản phẩm trong giỏ hàng
                    Optional<CartItem> cartItems = cartItemRepository.findByCartIdAndProductId(cart.getId(), billItem.getProductId());

                    if (cartItems.isPresent()) {

                        CartItem cartItem = cartItems.get();

                        // Nếu số lượng trong giỏ hàng nhỏ hơn hoặc bằng số lượng trong hóa đơn, xóa sản phẩm khỏi giỏ hàng
                        if (cartItem.getQuantity() <= billItem.getQuantity()) {
                            cartItemRepository.delete(cartItem);
                            continue;
                        }

                        // Nếu số lượng trong giỏ hàng lớn hơn số lượng trong hóa đơn, giảm số lượng sản phẩm trong giỏ hàng
                        cartItem.setQuantity(cartItem.getQuantity() - billItem.getQuantity());
                        cartItemRepository.save(cartItem);
                    }
                }
            }

            // Giảm số lượng mã giảm giá nếu có
            List<BillCoupons> billCoupons = billCouponsRepository.findByBillId(billId);
            for (BillCoupons billCoupon : billCoupons) {
                Coupon coupon = couponRepository.findById(billCoupon.getCouponId())
                        .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + billCoupon.getCouponId()));

                // Giảm số lượng mã giảm giá
                if (coupon.getQuantity() <= 0) {
                    throw new RuntimeException("Coupon is out of stock: " + coupon.getCode());
                }
                coupon.setQuantity(coupon.getQuantity() - 1);
                couponRepository.save(coupon);
            }
            
            // Cập nhật trạng thái hóa đơn
            bill.setStatus(BillStatus.SUCCESS);
            bill.setDescription("Payment successful");
            billRepository.save(bill);
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }

    @Override
    public List<BillResponse> getAllBills(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Lấy tất cả hóa đơn của người dùng
        List<Bill> bills = billRepository.findByUserId(user.getId());
        if (bills.isEmpty()) {
            return List.of(); // Trả về danh sách rỗng nếu không có hóa đơn nào
        }

        // Khởi tạo danh sách để lưu trữ các BillResponse trả về
        List<BillResponse> billResponses = new ArrayList<>();

        // Lấy ra cac thông tin hóa đơn và chuyển đổi sang BillResponse
        for (Bill bill : bills) {
            // Lấy danh sách sản phẩm trong hóa đơn
            List<BillItems> billItems = billItemsRepository.findByBillId(bill.getId());

            // Chuyển sang danh sách BillItemsResponse
            List<BillItemResponse> billItemResponses = billItems.stream()
                    .map(billItem -> BillItemResponse.builder()
                            .productId(billItem.getProductId())
                            .quantity(billItem.getQuantity())
                            .build())
                    .toList();

            // Lấy danh sách mã giảm giá trong hóa đơn
            List<BillCoupons> billCoupons = billCouponsRepository.findByBillId(bill.getId());

            // Chuyển sang danh sách mã giảm giá
            List<Long> couponIds = billCoupons.stream().map(BillCoupons::getCouponId).toList();

            // Tạo BillResponse từ hóa đơn
            BillResponse billResponse = BillResponse.builder()
                    .billId(bill.getId())
                    .status(bill.getStatus().toString())
                    .paymentUrl(bill.getPaymentUrl())
                    .createdAt(bill.getCreatedAt())
                    .updatedAt(bill.getUpdatedAt())
                    .billItems(billItemResponses)
                    .couponIds(couponIds)
                    .build();
            billResponses.add(billResponse);
        }

        // Map bills sang BillResponse
        return billResponses;
    }
}
