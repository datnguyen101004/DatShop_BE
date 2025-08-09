package com.dat.backend.datshop.chat.service;

import com.dat.backend.datshop.chat.dto.ConversationResponse;

public interface ConversationService {
    ConversationResponse getOrCreateConservation(Long receiverID, String name);
}
