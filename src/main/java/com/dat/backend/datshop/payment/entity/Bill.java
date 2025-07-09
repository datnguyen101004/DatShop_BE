package com.dat.backend.datshop.payment.entity;

import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "exchange")
public class Bill {
    @Id
    private String id;

    private Long userId;
    private Double price;
    private String description;
    private String paymentUrl;

    @Enumerated(value = jakarta.persistence.EnumType.STRING)
    private BillStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDateTime updatedAt;
}
