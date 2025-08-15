package com.dat.backend.datshop.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OllamaService ollamaService;
    private final QdrantService qdrantService;

    public String ask(String question) throws JsonProcessingException {
        // Chuyển đổi câu hỏi thành chữ vector
        double[] vectorQuestion = ollamaService.generateEmbedding(question).block();

        // Search top 5 sản phẩm tương tự trong Qdrant
        List<Map<String, Object>> top5Products = searchTop5Products(vectorQuestion);

        // Tạo câu trả lời dựa trên các sản phẩm tìm được bằng mistral
        return ollamaService.generateAnswer(question, top5Products).block();
    }

    private List<Map<String, Object>> searchTop5Products(double[] vectorQuestion) throws JsonProcessingException {
        List<Double> vectorList = Arrays.stream(vectorQuestion).boxed().toList();

        // Gọi api Qdrant để tìm kiếm các sản phẩm tương tự
        List<Map<String, Object>> response = qdrantService.searchTopKSimilarProducts(vectorList, 5);
        if (response == null || response.isEmpty()) {
            return null;
        }

        return response;
    }
}
