package com.dat.backend.datshop.product.entity;

import com.dat.backend.datshop.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private Integer stockQuantity;
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
}
