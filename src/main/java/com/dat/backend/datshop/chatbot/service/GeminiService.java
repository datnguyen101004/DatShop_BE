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
    private static final String RESPONSE_GUIDELINES = """
            You are DatShop's marketplace assistant.
            Answer in Vietnamese using clean GitHub-flavored Markdown.
            Start with the direct answer. Keep paragraphs short.
            Use ## headings only when the answer has multiple sections.
            Use bullet points for steps, options, or product features.
            Use a Markdown table only when comparing multiple items.
            Bold only the most important names, prices, or warnings.
            Do not output raw JSON or HTML. Do not use a code block unless the user asks for code.
            If the supplied context is insufficient, say so clearly instead of inventing facts.

            Relevant DatShop context:
            """;

    @Value("${GOOGLE.gemini-api-key}")
    private String googleApiKey;
    @Value("${ai.chat.gemini.model:gemini-3.6-flash}")
    private String geminiChatModel;
    @Value("${ai.chat.gemini.thinking-level:minimal}")
    private String thinkingLevel;
    private final InformationRepository informationRepository;

    public String generateResponse(String question, List<Map<String, Object>> top5Products) {
        // Tạo ngữ cảnh cho câu hỏi
        StringBuilder prompt = new StringBuilder();
        prompt.append(RESPONSE_GUIDELINES);

        for (Map<String, Object> productMap : top5Products) {
            informationRepository.findById(String.valueOf(productMap.get("id"))).ifPresent(product -> {
                prompt.append(String.format(
                        "Information ID: %s\nName: %s\nDescription: %s\nType: %s\n\n",
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getType()
                ));
            });
        }
        prompt.append("User question: ").append(question).append("\n");


        // Tạo câu trả lời dựa trên thông tin từ Gemini API
        Client client = Client.builder()
                .apiKey(googleApiKey)
                .build();

        // Sử dụng mô hình Gemini 2.0 Flash để tạo nội dung
        GenerateContentResponse response = client.models.generateContent(
                geminiChatModel,
                prompt.toString(), // Trả lời dựa trên ngữ cảnh đã tạo
                GenerateContentConfig.builder()
                        .thinkingConfig(ThinkingConfig.builder()
                                .thinkingLevel(thinkingLevel)
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
