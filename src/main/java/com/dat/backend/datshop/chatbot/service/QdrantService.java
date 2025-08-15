package com.dat.backend.datshop.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QdrantService {

    private final WebClient webClient;

    public void upsertVector(String id, double[] vec) throws JsonProcessingException {
        List<Double> vectorList = Arrays.stream(vec).boxed().toList();

//        Object payload = Map.of(
//                "points", List.of(Map.of(
//                        "id", id,
//                        "vector", Arrays.stream(vec).boxed().toList()
//                ))
//        );
//        System.out.println(new ObjectMapper().writeValueAsString(payload));

        webClient.put()
            .uri("http://localhost:6333/collections/chatbot_rag/points?wait=true")
            .bodyValue(Map.of(
                    "points", List.of(Map.of(
                            "id", id,
                            "vector", vectorList
                    ))
            ))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }


    public List<Map<String, Object>> searchTopKSimilarProducts(List<Double> vectorList, int i) throws JsonProcessingException {
        Object requestBody = Map.of(
                "query", vectorList,
                "limit", i,
                "params", Map.of(
                        "hnsw_ef", 128,
                        "exact", false
                )
        );

        System.out.println(new ObjectMapper().writeValueAsString(requestBody));

        Map response = webClient.post()
                .uri("http://localhost:6333/collections/chatbot_rag/points/query")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println(new ObjectMapper().writeValueAsString(response));
        if (response == null || response.isEmpty()) {
            return List.of();
        }

        Map<String, Object> resultMap = (Map<String, Object>) response.get("result");

        return (List<Map<String, Object>>) resultMap.get("points");
    }
}
