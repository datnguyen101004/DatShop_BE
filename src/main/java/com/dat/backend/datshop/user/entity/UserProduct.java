package com.dat.backend.datshop.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "user_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProduct {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    private Long userId;
    private Long productId;

    @CreationTimestamp
    private LocalDateTime lastModified;
}
