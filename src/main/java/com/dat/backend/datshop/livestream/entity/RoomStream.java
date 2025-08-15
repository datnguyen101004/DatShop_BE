package com.dat.backend.datshop.livestream.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RoomStream {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String streamUrl; // URL của luồng livestream
    private String streamKey; // Khóa stream để phát trực tiếp
    private String likeCount;
    private String viewCount;
    private String commentCount;
    private String authorId; // ID của người dùng tạo room
    private String chatId; // Id của từng phần chat
}
