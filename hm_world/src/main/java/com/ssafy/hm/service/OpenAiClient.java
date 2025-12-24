package com.ssafy.hm.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenAiClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.baseUrl}")
    private String baseUrl;

    @Value("${openai.apiKey}")
    private String apiKey;

    public String generateCaption(String base64DataUrl) {
        String url = baseUrl + "/v1/chat/completions";
        Map<String, Object> payload = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(Map.of(
                "role", "user",
                "content", List.of(
                    Map.of("type", "text", "text", "Describe the product image with shape, material, and color only. Do not mention brand or logo."),
                    Map.of("type", "image_url", "image_url", Map.of("url", base64DataUrl))
                )
            ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ResponseEntity<Map> res = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) res.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    public List<Double> embedText(String text) {
        String url = baseUrl + "/v1/embeddings";
        Map<String, Object> payload = Map.of(
            "model", "text-embedding-3-large",
            "input", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ResponseEntity<Map> res = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Map.class
        );

        List<Map<String, Object>> data = (List<Map<String, Object>>) res.getBody().get("data");
        return (List<Double>) data.get(0).get("embedding");
    }
}
