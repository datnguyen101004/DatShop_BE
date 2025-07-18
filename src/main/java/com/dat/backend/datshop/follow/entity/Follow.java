package com.dat.backend.datshop.follow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_type")
    private FollowType followType; // e.g., "shop", "product", ...

    private boolean isDeleted = false; // Cờ dùng để đánh dấu theo dõi đã bị xóa hay chưa
}
