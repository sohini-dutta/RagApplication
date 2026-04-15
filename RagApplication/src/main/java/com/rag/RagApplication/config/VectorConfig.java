package com.rag.RagApplication.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;

@Configuration
public class VectorConfig {

    @Value("${app.vectorstore.path:vectorstore.json}")
    private String vectorStorePath;

    @Bean
    public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        // 1. Create the store using the Builder (Fixes the 'protected access' error)
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // 2. Handle initialization logic (Load existing data)
        File file = new File(vectorStorePath);
        if (file.exists() && file.length() > 0) {
            try {
                store.load(file);
            } catch (Exception e) {
                // In production, log the error and decide if you want to fail fast
                // or continue with an empty store.
                System.err.println("Failed to load vector store: " + e.getMessage());
            }
        }
        return store;
    }
}