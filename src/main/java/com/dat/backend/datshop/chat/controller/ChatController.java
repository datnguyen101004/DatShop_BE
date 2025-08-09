package com.dat.backend.datshop.chat.controller;

import com.dat.backend.datshop.chat.dto.SendMessage;
import com.dat.backend.datshop.chat.service.ChatService;
import com.dat.backend.datshop.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{conversationId}") // Client gửi tin nhắn vào endpoint /app/chat ở client
    @SendTo("/topic/{conversationId}") // Tất cả người dùng đăng ký với /topic/messages sẽ nhận được tin nhắn này
    public ApiResponse<String> sendMessage(SendMessage sendMessageRequest, @DestinationVariable String conversationId, Principal principal) {
        return ApiResponse.success(chatService.sendMessage(sendMessageRequest, conversationId, principal.getName()));
    }
}
