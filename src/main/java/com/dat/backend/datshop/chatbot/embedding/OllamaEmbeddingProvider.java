package com.dat.backend.datshop.chatbot.embedding;

import com.dat.backend.datshop.chatbot.service.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OllamaEmbeddingProvider implements EmbeddingProvider {
    private final OllamaService ollamaService;

    @Value("${ai.embedding.ollama.collection:chatbot_rag}")
    private String collectionName;

    @Override
    public String getName() {
        return "ollama";
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public double[] embedDocument(String text) {
        return generateEmbedding(text);
    }

    @Override
    public double[] embedQuery(String text) {
        return generateEmbedding(text);
    }

    private double[] generateEmbedding(String text) {
        return Objects.requireNonNull(
                ollamaService.generateEmbedding(text).block(),
                "Ollama did not return an embedding"
        );
    }
}
