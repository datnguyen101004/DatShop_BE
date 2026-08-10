package com.dat.backend.datshop.chatbot.embedding;

import com.dat.backend.datshop.chatbot.entity.Information;
import com.dat.backend.datshop.chatbot.repository.InformationRepository;
import com.dat.backend.datshop.chatbot.service.QdrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ai.embedding.reindex-on-startup", havingValue = "true")
public class EmbeddingReindexRunner implements ApplicationRunner {
    private final InformationRepository informationRepository;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String collectionName = embeddingService.getCollectionName();
        int indexedCount = 0;

        log.info("Re-indexing chatbot information with {} into {}",
                embeddingService.getProviderName(), collectionName);

        for (Information information : informationRepository.findAll()) {
            String text = String.format(
                    "Type: %s, Name: %s, Description: %s",
                    information.getType(), information.getName(), information.getDescription()
            );
            double[] vector = embeddingService.embedDocument(text);
            qdrantService.upsertVector(collectionName, information.getId(), vector);
            indexedCount++;
        }

        log.info("Re-indexed {} chatbot information records into {}", indexedCount, collectionName);
    }
}
