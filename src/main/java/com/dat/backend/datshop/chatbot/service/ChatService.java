package com.dat.backend.datshop.chatbot.service;

import com.dat.backend.datshop.chatbot.embedding.EmbeddingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private final GeminiService geminiService;

    private List<Map<String, Object>> searchTop5Products(double[] vectorQuestion) throws JsonProcessingException {
        List<Double> vectorList = Arrays.stream(vectorQuestion).boxed().toList();

        // Gọi api Qdrant để tìm kiếm các sản phẩm tương tự
        List<Map<String, Object>> response = qdrantService.searchTopKSimilarProducts(
                embeddingService.getCollectionName(), vectorList, 5
        );
        if (response == null || response.isEmpty()) {
            return List.of();
        }

        return response;
    }

    public String askGemini(String question) throws JsonProcessingException {
        // Chuyển đổi câu hỏi thành chữ vector
        double[] vectorQuestion = embeddingService.embedQuery(question);

        // Search top 5 sản phẩm tương tự trong Qdrant
        List<Map<String, Object>> top5Products = searchTop5Products(vectorQuestion);

        // Tạo câu trả lời dựa trên các sản phẩm tìm được bằng Gemini
        return geminiService.generateResponse(question, top5Products);
    }
}
