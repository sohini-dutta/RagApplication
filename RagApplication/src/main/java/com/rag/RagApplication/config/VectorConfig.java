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
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        File file = new File(vectorStorePath);
        if (file.exists() && file.length() > 0) {
            try {
                store.load(file);
            } catch (Exception e) {
                System.err.println("Failed to load vector store: " + e.getMessage());
            }
        }
        return store;
    }
}