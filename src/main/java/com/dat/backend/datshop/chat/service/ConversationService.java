package com.dat.backend.datshop.chat.service;

import com.dat.backend.datshop.chat.dto.ConversationResponse;

import java.util.List;

public interface ConversationService {
    ConversationResponse getOrCreateConservation(Long receiverID, String name);

    List<ConversationResponse> getAllConversations(String name);

    ConversationResponse getConversation(String conversationId, String name);
}
