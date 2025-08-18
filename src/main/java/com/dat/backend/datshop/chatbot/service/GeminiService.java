package com.dat.backend.datshop.chatbot.service;

import com.dat.backend.datshop.chatbot.repository.InformationRepository;
import com.google.genai.Client;
import com.google.genai.types.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Value("${GOOGLE.gemini-api-key}")
    private String googleApiKey;
    private final InformationRepository informationRepository;

    public String generateResponse(String question, List<Map<String, Object>> top5Products) {
        // Tạo ngữ cảnh cho câu hỏi
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful assistant that answers questions about products.\n");
        prompt.append("Here are the top 5 products:\n");

        for (Map<String, Object> productMap : top5Products) {
            informationRepository.findById(String.valueOf(productMap.get("id"))).ifPresent(product -> {
                prompt.append(String.format(
                        "Information ID: %s\nName: %s\nDescription: %s\nPrice: %s\n\n",
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getType()
                ));
            });
        }
        prompt.append("Now, answer the question at vietnamese language: ").append(question).append("\n");


        // Tạo câu trả lời dựa trên thông tin từ Gemini API
        Client client = Client.builder()
                .apiKey(googleApiKey)
                .build();

        // Sử dụng mô hình Gemini 2.0 Flash để tạo nội dung
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.0-flash", // Model gemini
                prompt.toString(), // Trả lời dựa trên ngữ cảnh đã tạo
                GenerateContentConfig.builder()
                        .thinkingConfig(ThinkingConfig.builder()
                                .thinkingBudget(0)
                                .build())
                        .build()
        );

        // Trả về câu trả lời đầu tiên từ phản hồi
        Optional<List<Candidate>> candidates = response.candidates();
        // Kiểm tra xem candidates có tồn tại không
        if (candidates.isPresent()) {
            // Nếu có candidates, lấy câu trả lời đầu tiên
            List<Candidate> candidateList = candidates.get();
            Candidate candidate = candidateList.getFirst();
            Optional<Content> content = candidate.content();
            // Kiểm tra xem content có tồn tại không
            if (content.isPresent()) {
                // Nếu có content, lấy phần đầu tiên và trả về văn bản
                Optional<List<Part>> parts = content.get().parts();
                if (parts.isPresent()) {
                    Optional<String> text = parts.get().getFirst().text();
                    // Trả về văn bản đã được cắt bỏ khoảng trắng
                    if (text.isPresent()) {
                        return text.get().trim();
                    }
                }
            }
        }
        // Nếu không có candidates hoặc không có content, trả về câu trả lời mặc định
        return "Xin lỗi, tôi không thể trả lời câu hỏi của bạn ngay bây giờ. Vui lòng thử lại sau.";
    }
}
