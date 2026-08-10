package com.dat.backend.datshop;

import com.dat.backend.datshop.chatbot.embedding.EmbeddingProvider;
import com.dat.backend.datshop.chatbot.embedding.EmbeddingService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmbeddingServiceTest {
    @Test
    void selectsConfiguredProvider() {
        EmbeddingService service = new EmbeddingService(List.of(
                provider("ollama", "chatbot_rag", new double[]{1.0}),
                provider("gemini", "chatbot_rag_gemini", new double[]{2.0})
        ), "gemini");

        assertEquals("gemini", service.getProviderName());
        assertEquals("chatbot_rag_gemini", service.getCollectionName());
        assertArrayEquals(new double[]{2.0}, service.embedDocument("product"));
        assertArrayEquals(new double[]{2.0}, service.embedQuery("question"));
    }

    @Test
    void rejectsUnknownProvider() {
        assertThrows(IllegalStateException.class, () -> new EmbeddingService(
                List.of(provider("ollama", "chatbot_rag", new double[]{1.0})),
                "unknown"
        ));
    }

    private EmbeddingProvider provider(String name, String collection, double[] vector) {
        return new EmbeddingProvider() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getCollectionName() {
                return collection;
            }

            @Override
            public double[] embedDocument(String text) {
                return vector;
            }

            @Override
            public double[] embedQuery(String text) {
                return vector;
            }
        };
    }
}
