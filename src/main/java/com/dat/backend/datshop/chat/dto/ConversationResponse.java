package com.dat.backend.datshop.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {
    private String conversationId;
    private Long user1Id; // ID của người dùng 1
    private Long user2Id; // ID của người dùng 2
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatarUrl;
    private LocalDateTime lastMessageAt;
    private List<MessageResponse> listMessages;
}
