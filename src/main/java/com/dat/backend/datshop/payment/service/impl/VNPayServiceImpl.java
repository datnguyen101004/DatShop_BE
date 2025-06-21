package com.dat.backend.datshop.payment.service.impl;

import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.payment.config.VNPayConfig;
import com.dat.backend.datshop.payment.dto.BillResponse;
import com.dat.backend.datshop.payment.dto.PayRequest;
import com.dat.backend.datshop.payment.entity.Bill;
import com.dat.backend.datshop.payment.entity.BillStatus;
import com.dat.backend.datshop.payment.entity.Bill_Items;
import com.dat.backend.datshop.payment.repository.BillRepository;
import com.dat.backend.datshop.payment.repository.Bill_ItemsRepository;
import com.dat.backend.datshop.payment.service.VNPayService;
import com.dat.backend.datshop.payment.util.VNPayUtil;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {
    private final VNPayConfig vnPayConfig;
    private final UserRepository userRepository;
    private final BillRepository billRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final Bill_ItemsRepository billItemsRepository;

    @Override
    @Transactional
    public BillResponse createPayment(List<PayRequest> payRequestList, HttpServletRequest request, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Calculate total amount
        double amount = payRequestList.stream()
                .mapToLong(payRequest -> {
                    Product product = productRepository.findById(payRequest.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + payRequest.getProductId()));
                    return (long) (product.getPrice() * payRequest.getQuantity());
                })
                .sum();

        // Create bill entity
        Bill bill = Bill.builder()
                .userId(user.getId())
                .price(amount)
                .status(BillStatus.PENDING)
                .description("Waiting for payment")
                .build();
        billRepository.save(bill);


        // Check stock
        for (PayRequest payRequest : payRequestList) {
            Product product = productRepository.findById(payRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + payRequest.getProductId()));
            if (product.getStockQuantity() < payRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product ID: " + payRequest.getProductId());
            }

            // Add product order to bill items
            Bill_Items billItem = Bill_Items.builder()
                    .billId(bill.getId())
                    .productId(payRequest.getProductId())
                    .quantity(payRequest.getQuantity())
                    .build();

            billItemsRepository.save(billItem);
        }

        // Create payment URL
        String ipAddr = VNPayUtil.getIpAddr(request);
        Map<String, String> vnpParamsMap = vnPayConfig.createVnPayUrl(bill.getId());
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount*100L));
        vnpParamsMap.put("vnp_IpAddr", ipAddr);
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.vnp_HashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Create BillResponse
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
        String billIdStr = request.getParameter("billId");
        Long billId = Long.parseLong(billIdStr);
        if (status.equals("00")) {
            /*             * Payment successful
             * Update bill status to PAID and reduce stock quantity
             */

            // Fetch bill
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));

            Cart cart = cartRepository.findByUserId(bill.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + bill.getUserId()));

            // Remove bill items from the bill
            List<Bill_Items> billItems = billItemsRepository.findByBillId(billId);
            for (Bill_Items billItem : billItems) {
                // Fetch product
                Product product = productRepository.findById(billItem.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + billItem.getProductId()));

                // Reduce stock quantity
                if (product.getStockQuantity() < billItem.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product ID: " + billItem.getProductId());
                }
                product.setStockQuantity(product.getStockQuantity() - billItem.getQuantity());
                productRepository.save(product);

                // Update cart item quantity
                CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), billItem.getProductId())
                        .orElseThrow(() -> new RuntimeException("Cart item not found for product ID: " + billItem.getProductId()));
                cartItem.setQuantity(cartItem.getQuantity() - billItem.getQuantity());
                if (cartItem.getQuantity() <= 0) {
                    // Remove cart item if quantity is zero or less
                    cartItemRepository.delete(cartItem);
                } else {
                    // Save updated cart item
                    cartItemRepository.save(cartItem);
                }

                // Remove bill item
                billItemsRepository.delete(billItem);
            }

            if (bill.getStatus() == BillStatus.SUCCESS) {
                return "Already processed";
            }
            bill.setStatus(BillStatus.SUCCESS);
            bill.setDescription("Payment successful");
            billRepository.save(bill);
            return "Success";
        } else {
            return "Failed";
        }
    }
}
