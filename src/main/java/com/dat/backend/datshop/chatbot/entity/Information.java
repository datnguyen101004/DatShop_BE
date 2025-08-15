package com.dat.backend.datshop.chatbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Information {
    @Id
    @UuidGenerator
    private String id;

    @Column(length = 2000)
    private String description;

    private String name; // Tên thông tin

    private InforType type; // Loại thông tin, bao gồm: PRODUCT, DELIVERY, PAYMENT, ORDER
}
