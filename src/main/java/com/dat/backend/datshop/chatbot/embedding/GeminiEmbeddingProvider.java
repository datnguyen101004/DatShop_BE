package com.dat.backend.datshop.chatbot.embedding;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiEmbeddingProvider implements EmbeddingProvider {
    private final Client client;
    private final String model;
    private final int outputDimension;
    private final String collectionName;

    public GeminiEmbeddingProvider(
            @Value("${GOOGLE.gemini-api-key}") String apiKey,
            @Value("${ai.embedding.gemini.model:gemini-embedding-001}") String model,
            @Value("${ai.embedding.gemini.dimension:768}") int outputDimension,
            @Value("${ai.embedding.gemini.collection:chatbot_rag_gemini}") String collectionName
    ) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.model = model;
        this.outputDimension = outputDimension;
        this.collectionName = collectionName;
    }

    @Override
    public String getName() {
        return "gemini";
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public double[] embedDocument(String text) {
        return embed(text, "RETRIEVAL_DOCUMENT");
    }

    @Override
    public double[] embedQuery(String text) {
        return embed(text, "RETRIEVAL_QUERY");
    }

    private double[] embed(String text, String taskType) {
        EmbedContentConfig config = EmbedContentConfig.builder()
                .taskType(taskType)
                .outputDimensionality(outputDimension)
                .build();

        EmbedContentResponse response = client.models.embedContent(model, text, config);
        List<ContentEmbedding> embeddings = response.embeddings()
                .orElseThrow(() -> new IllegalStateException("Gemini did not return an embedding"));

        if (embeddings.isEmpty()) {
            throw new IllegalStateException("Gemini returned an empty embedding list");
        }

        List<Float> values = embeddings.getFirst().values()
                .orElseThrow(() -> new IllegalStateException("Gemini embedding has no values"));

        return values.stream().mapToDouble(Float::doubleValue).toArray();
    }

    @PreDestroy
    public void close() {
        client.close();
    }
}
