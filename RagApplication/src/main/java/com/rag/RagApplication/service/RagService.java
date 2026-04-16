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
import java.io.File;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;

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
        // Library youtube-transcript-api
        String transcript = fetchTranscript(videoUrl);
        Document document = new Document(transcript, Map.of("source", videoUrl));
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(List.of(document));
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
                .collect(joining("\n"));
        return chatClient.prompt()
                .user(u -> u.text("Answer the question based on the context:\n\n{context}\n\nQuestion: {query}")
                        .param("context", content)
                        .param("query", message))
                .call()
                .content();
    }
    private String fetchTranscript(String videoId) {
        try {
            YoutubeTranscriptApi youtubeTranscriptApi = TranscriptApiFactory.createDefault();
            TranscriptList transcriptList = youtubeTranscriptApi.listTranscripts(videoId);
            Transcript transcriptObj = transcriptList.findManualTranscript("en");
            TranscriptContent content = transcriptObj.fetch();
            StringBuilder transcriptText = new StringBuilder();
            for(int i=0 ; i<content.getContent().size();i++){
                transcriptText.append(content.getContent().get(i).getText());
            }
            return transcriptText.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
