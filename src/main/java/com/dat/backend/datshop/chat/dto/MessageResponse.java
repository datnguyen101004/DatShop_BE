package com.dat.backend.datshop.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String conversationId;
    private Long senderId;
    private String message;
    private boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime removedAt;
}
