package com.ssafy.hm.service;

import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AiCategoryServiceImpl implements AiCategoryService {
    private static final Set<String> ALLOWED = Set.of("의류", "악세사리", "인형", "휴대폰");
    private final OpenAiClient openAiClient;

    public AiCategoryServiceImpl(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public String classifyImageCategory(String imageDataUrl) throws Exception {
        String raw = openAiClient.classifyImageCategory(imageDataUrl);
        String normalized = raw.replaceAll("[^\\uAC00-\\uD7A3]", "");
        if (normalized.isBlank()) {
            normalized = "악세사리";
        }
        if ("액세서리".equals(normalized)) {
            normalized = "악세사리";
        }
        if (!ALLOWED.contains(normalized)) {
            normalized = "악세사리";
        }
        return normalized;
    }
}
