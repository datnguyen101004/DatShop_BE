package com.dat.backend.datshop.livestream.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mapstruct.Mapper;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ChatStream {
    @Id
    private String chatId; // ID của cuộc trò chuyện
    private String authorId;
    private String message; // Nội dung tin nhắn
    private String roomStreamId; // ID của phòng livestream

    @CreationTimestamp
    private LocalDateTime createdAt; // Thời gian tạo tin nhắn
    @UpdateTimestamp
    private LocalDateTime updatedAt; // Thời gian cập nhật tin nhắn
}
