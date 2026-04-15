package com.rag.RagApplication.service;

import io.github.thoroldvix.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.ai.vectorstore.SearchRequest.*;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("${app.vectorstore.path}")
    private String vectorStorePath;

    public RagService(ChatClient.Builder builder,VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public void indexYouTubeVideo(String videoUrl){
        // 1. Fetch transcript (Pseudo-logic: Use an library like youtube-transcript-api)
        String transcript = fetchTranscript(videoUrl);

        // 2. Wrap in Document
        Document document = new Document(transcript, Map.of("source", videoUrl));

        // 3. Split into chunks for better retrieval
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(List.of(document));
        // 4. Store in Vector DB
        vectorStore.add(chunks);
        ((SimpleVectorStore) vectorStore).save(new File(vectorStorePath));
    }

    public String askQuestion(String message) {
        // Retrieve relevant chunks
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(message).topK(3).build()
        );

        assert similarDocuments != null;
        String content = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // Augment Prompt
        return chatClient.prompt()
                .user(u -> u.text("Answer the question based on the context:\n\n{context}\n\nQuestion: {query}")
                        .param("context", content)
                        .param("query", message))
                .call()
                .content();
    }

    private String fetchTranscript(String videoId) {
        try {

            // 2. Initialize the API via Factory
            YoutubeTranscriptApi youtubeTranscriptApi = TranscriptApiFactory.createDefault();

            // 3. List available transcripts for the video
            System.out.println(videoId);
            TranscriptList transcriptList = youtubeTranscriptApi.listTranscripts(videoId);
            System.out.println(transcriptList.findGeneratedTranscript("en").fetch());
            return "";
        } catch (Exception e) {
            // Wrap any underlying API/Parsing errors in your custom exception
            throw new RuntimeException(e);
        }
    }
}
