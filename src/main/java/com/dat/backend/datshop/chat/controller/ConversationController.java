package com.dat.backend.datshop.chat.controller;


import com.dat.backend.datshop.chat.dto.ConversationResponse;
import com.dat.backend.datshop.chat.service.ConversationService;
import com.dat.backend.datshop.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/create")
    public ApiResponse<ConversationResponse> createRoom(@RequestBody Long receiverId, Authentication authentication) {
        return ApiResponse.success(conversationService.getOrCreateConservation(receiverId, authentication.getName()));
    }

    @PostMapping("/support")
    public ApiResponse<ConversationResponse> getOrCreateSupportConversation(Authentication authentication) {
        return ApiResponse.success(conversationService.getOrCreateSupportConversation(authentication.getName()));
    }

    @GetMapping("/support/inbox")
    public ApiResponse<List<ConversationResponse>> getSupportInbox(Authentication authentication) {
        return ApiResponse.success(conversationService.getSupportInbox(authentication.getName()));
    }

    @GetMapping("/all")
    public ApiResponse<List<ConversationResponse>> getAllConversations(Authentication authentication) {
        return ApiResponse.success(conversationService.getAllConversations(authentication.getName()));
    }

    @GetMapping("/{conversationId}")
    public ApiResponse<ConversationResponse> getConversation(@PathVariable String conversationId, Authentication authentication) {
        return ApiResponse.success(conversationService.getConversation(conversationId, authentication.getName()));
    }
}
