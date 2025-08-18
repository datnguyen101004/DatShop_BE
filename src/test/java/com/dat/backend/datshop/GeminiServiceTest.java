package com.dat.backend.datshop;

import com.dat.backend.datshop.chatbot.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GeminiServiceTest {

    @Autowired
    private GeminiService geminiService;

    @Test
    void testGenerateResponse() {
        // Question
        String question = "How AI works?";


        // Gen response using GeminiService
        //String result = geminiService.generateResponse(question);

        //assertNotNull(result);
       // System.out.println("Gemini response: " + result);
    }
}
