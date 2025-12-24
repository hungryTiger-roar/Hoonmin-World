package com.ssafy.hm.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.hm.service.AiVisionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ai")
public class AiVisionController {

    private final AiVisionService aiVisionService;

    public AiVisionController(AiVisionService aiVisionService) {
        this.aiVisionService = aiVisionService;
    }

    @PostMapping(
        value = "/image-to-text",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> imageToText(
        @RequestParam("file") MultipartFile file
    ) throws Exception {


        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "이미지 파일이 필요합니다."));
        }

        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        String mime = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        String dataUrl = "data:" + mime + ";base64," + base64;

        String result = aiVisionService.analyzeImage(dataUrl);

        return ResponseEntity.ok(
            Map.of(
                "objects", result
            )
        );
    }
}
