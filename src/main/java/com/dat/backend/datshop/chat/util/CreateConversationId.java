package com.dat.backend.datshop.chat.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CreateConversationId {
    public String createConversationId(Long senderId, Long receiverId) {
        return "datshop_" + senderId + "_" + receiverId;
    }

    public String createRoomNameWithProduct(Long senderId, Long receiverId, Long productId) {
        return "datshop_" + senderId + "_" + receiverId + "_" + productId;
    }
}
