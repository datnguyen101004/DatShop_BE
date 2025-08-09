package com.dat.backend.datshop.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "message")
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    private String message;

    private Long senderId; // ID của người gửi
    private Long receiverId; // ID của người nhận

    private boolean isRead = false; // Trạng thái đã đọc

    @CreationTimestamp
    private LocalDateTime sentAt;

    private LocalDateTime removedAt;
}
