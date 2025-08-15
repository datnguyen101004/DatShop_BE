package com.dat.backend.datshop.chatbot.service;

import com.dat.backend.datshop.chatbot.repository.InformationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OllamaService {
    private final WebClient webClient;
    private final InformationRepository informationRepository;

    @Value("${ollama.embed-model}")
    private String ollamaEmbedModel;
    @Value("${ollama.chat-model}")
    private String ollamaChatModel;

    public Mono<double[]> generateEmbedding(String text) {
        Map<String, String> request = Map.of(
                "model", ollamaEmbedModel,
                "prompt", text
        );

        return webClient.post()
                .uri("http://localhost:11434/api/embeddings")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    JsonNode arr = node.get("embedding");
                    double[] vec = new double[arr.size()];
                    for (int i = 0; i < arr.size(); i++) vec[i] = arr.get(i).asDouble();
                    return vec;
                });
    }

    public Mono<String> generateAnswer(String question, List<Map<String, Object>> top5Products) {
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
        prompt.append("Now, answer the question: ").append(question).append("\n");

        Map<String, Object> request = Map.of(
                "model", ollamaChatModel,
                "prompt", prompt.toString(),
                "stream", false, // Quan trọng: để Ollama trả về 1 JSON đầy đủ
                "options", Map.of(
                        "num_predict", 128
                )
        );

        return webClient.post()
                .uri("http://localhost:11434/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> Optional.ofNullable(node.get("response"))
                        .map(JsonNode::asText)
                        .orElse("No response from model"));
    }
}
