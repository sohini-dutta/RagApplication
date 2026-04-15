package com.rag.RagApplication.controller;

import com.rag.RagApplication.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/index")
    public ResponseEntity<String> indexVideo(@RequestBody String url) {
        ragService.indexYouTubeVideo(url);
        return ResponseEntity.ok("Video indexed successfully!");
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String question) {
        return ResponseEntity.ok(ragService.askQuestion(question));
    }
}
