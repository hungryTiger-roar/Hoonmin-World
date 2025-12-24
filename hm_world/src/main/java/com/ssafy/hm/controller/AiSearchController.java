package com.ssafy.hm.controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.hm.service.AiSearchService;

@RestController
@CrossOrigin("*")
public class AiSearchController {
    private final AiSearchService aiSearchService;

    public AiSearchController(AiSearchService aiSearchService) {
        this.aiSearchService = aiSearchService;
    }

    @PostMapping(
        value = {"/ai/search-by-image", "/api/ai/search-by-image"},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<Map<String, Object>>> search(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "upload_file", required = false) MultipartFile uploadFile,
        @RequestParam(defaultValue = "5") int topK
    ) throws Exception {
        MultipartFile target = (file != null && !file.isEmpty()) ? file : uploadFile;
        if (target == null || target.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());
        }
        String base64 = Base64.getEncoder().encodeToString(target.getBytes());
        String mime = target.getContentType() != null ? target.getContentType() : "image/jpeg";
        String dataUrl = "data:" + mime + ";base64," + base64;
        return ResponseEntity.ok(aiSearchService.searchByImageBase64(dataUrl, topK));
    }

    @PostMapping("/ai/embeddings/rebuild")
    public ResponseEntity<Map<String, Object>> rebuild(
        @RequestParam(defaultValue = "missing") String mode,
        @RequestParam(defaultValue = "0") int limit
    ) throws Exception {
        return ResponseEntity.ok(aiSearchService.rebuildEmbeddings(mode, limit));
    }
}
