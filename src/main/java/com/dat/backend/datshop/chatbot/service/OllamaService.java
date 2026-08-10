package com.dat.backend.datshop.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OllamaService {
    private final WebClient webClient;

    @Value("${ollama.embed-model}")
    private String ollamaEmbedModel;

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

}
