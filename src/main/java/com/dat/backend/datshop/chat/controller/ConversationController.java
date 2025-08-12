package com.dat.backend.datshop.chat.controller;


import com.dat.backend.datshop.chat.dto.ConversationResponse;
import com.dat.backend.datshop.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/create")
    public ResponseEntity<ConversationResponse> createRoom(@RequestBody Long receiverId, Authentication authentication) {
        return ResponseEntity.ok(conversationService.getOrCreateConservation(receiverId, authentication.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ConversationResponse>> getAllConversations(Authentication authentication) {
        return ResponseEntity.ok(conversationService.getAllConversations(authentication.getName()));
    }
}
