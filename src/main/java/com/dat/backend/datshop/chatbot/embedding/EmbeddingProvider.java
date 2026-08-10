package com.dat.backend.datshop.chatbot.embedding;

public interface EmbeddingProvider {
    String getName();

    String getCollectionName();

    double[] embedDocument(String text);

    double[] embedQuery(String text);
}
