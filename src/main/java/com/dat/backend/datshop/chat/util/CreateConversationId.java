package com.dat.backend.datshop.chat.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CreateConversationId {
    public String createConversationId(Long senderId, Long receiverId) {
        long firstId = Math.min(senderId, receiverId);
        long secondId = Math.max(senderId, receiverId);
        return "datshop_" + firstId + "_" + secondId;
    }

    public String createRoomNameWithProduct(Long senderId, Long receiverId, Long productId) {
        return "datshop_" + senderId + "_" + receiverId + "_" + productId;
    }
}
