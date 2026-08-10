package com.dat.backend.datshop.chat.service;

import com.dat.backend.datshop.chat.dto.SendMessage;
import com.dat.backend.datshop.chat.dto.MessageResponse;

public interface ChatService {
    MessageResponse sendMessage(SendMessage sendMessageRequest, String conversationId, String senderEmail);
}
