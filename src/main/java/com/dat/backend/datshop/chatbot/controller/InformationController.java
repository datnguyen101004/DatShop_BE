package com.dat.backend.datshop.chatbot.controller;

import com.dat.backend.datshop.chatbot.dto.CreateInformation;
import com.dat.backend.datshop.chatbot.dto.InformationResponse;
import com.dat.backend.datshop.chatbot.entity.Information;
import com.dat.backend.datshop.chatbot.service.InformationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/information")
public class InformationController {
    private final InformationService productService;

    @PostMapping("/create")
    public ResponseEntity<InformationResponse> create(@RequestBody CreateInformation createInformation) throws JsonProcessingException {
        return ResponseEntity.ok(productService.create(createInformation));
    }
}
