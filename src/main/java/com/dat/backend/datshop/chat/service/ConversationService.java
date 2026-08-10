package com.dat.backend.datshop.chat.service;

import com.dat.backend.datshop.chat.dto.ConversationResponse;

import java.util.List;

public interface ConversationService {
    ConversationResponse getOrCreateConservation(Long receiverID, String name);

    ConversationResponse getOrCreateSupportConversation(String name);

    List<ConversationResponse> getAllConversations(String name);

    List<ConversationResponse> getSupportInbox(String name);

    ConversationResponse getConversation(String conversationId, String name);
}
