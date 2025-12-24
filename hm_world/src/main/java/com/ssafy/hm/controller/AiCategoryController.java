package com.ssafy.hm.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.ssafy.hm.service.AiCategoryService;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI 기반 API")
public class AiCategoryController {
    private final AiCategoryService aiCategoryService;

    public AiCategoryController(AiCategoryService aiCategoryService) {
        this.aiCategoryService = aiCategoryService;
    }

    @PostMapping(value = "/image-category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지로 상품 카테고리 분류")
    public ResponseEntity<Map<String, String>> classify(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "NO_IMAGE"));
        }
        String mime = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        String dataUrl = "data:" + mime + ";base64," + base64;
        String category = aiCategoryService.classifyImageCategory(dataUrl);
        return ResponseEntity.ok(Map.of("category", category));
    }
}
