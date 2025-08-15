package com.dat.backend.datshop.chatbot.controller;

import com.dat.backend.datshop.chatbot.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String question) throws JsonProcessingException {
        return ResponseEntity.ok(chatService.ask(question));
    }
}
