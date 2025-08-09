package com.dat.backend.datshop.chat.service;

import com.dat.backend.datshop.chat.dto.SendMessage;

import java.security.Principal;

public interface ChatService {
    String sendMessage(SendMessage sendMessageRequest, String conversationId, String senderEmail);
}
