package com.dat.backend.datshop.chatbot.embedding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmbeddingService {
    private final Map<String, EmbeddingProvider> providers;
    private final String activeProviderName;

    public EmbeddingService(
            List<EmbeddingProvider> providers,
            @Value("${ai.embedding.provider:gemini}") String activeProviderName
    ) {
        this.providers = providers.stream().collect(Collectors.toUnmodifiableMap(
                provider -> provider.getName().toLowerCase(Locale.ROOT),
                Function.identity()
        ));
        this.activeProviderName = activeProviderName.toLowerCase(Locale.ROOT);

        EmbeddingProvider activeProvider = getActiveProvider();
        log.info("Using {} embedding provider with Qdrant collection {}",
                activeProvider.getName(), activeProvider.getCollectionName());
    }

    public double[] embedDocument(String text) {
        return getActiveProvider().embedDocument(text);
    }

    public double[] embedQuery(String text) {
        return getActiveProvider().embedQuery(text);
    }

    public String getCollectionName() {
        return getActiveProvider().getCollectionName();
    }

    public String getProviderName() {
        return getActiveProvider().getName();
    }

    private EmbeddingProvider getActiveProvider() {
        EmbeddingProvider provider = providers.get(activeProviderName);
        if (provider == null) {
            throw new IllegalStateException(
                    "Unknown embedding provider '%s'. Available providers: %s"
                            .formatted(activeProviderName, providers.keySet())
            );
        }
        return provider;
    }
}
