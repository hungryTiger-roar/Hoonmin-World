package com.ssafy.hm.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.hm.dto.Item;
import com.ssafy.hm.dto.ItemEmbedding;
import com.ssafy.hm.repo.ItemEmbeddingRepo;
import com.ssafy.hm.repo.ItemRepo;

@Service
public class AiSearchService {

    private final OpenAiClient openAiClient;
    private final ItemEmbeddingRepo itemEmbeddingRepo;
    private final ItemRepo itemRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiSearchService(OpenAiClient openAiClient, ItemEmbeddingRepo itemEmbeddingRepo, ItemRepo itemRepo) {
        this.openAiClient = openAiClient;
        this.itemEmbeddingRepo = itemEmbeddingRepo;
        this.itemRepo = itemRepo;
    }

    public List<Map<String, Object>> searchByImageBase64(String base64DataUrl, int topK) throws Exception {
        String caption = openAiClient.generateCaption(base64DataUrl);
        List<Double> queryEmbedding = openAiClient.embedText(caption);

        List<ItemEmbedding> items = itemEmbeddingRepo.findAll();
        List<Map<String, Object>> scored = new ArrayList<>();

        for (ItemEmbedding item : items) {
            List<Double> emb = objectMapper.readValue(item.getEmbeddingJson(), new TypeReference<List<Double>>() {});
            double score = cosine(queryEmbedding, emb);
            Map<String, Object> entry = new HashMap<>();
            entry.put("itemId", item.getItemId());
            entry.put("score", score);
            scored.add(entry);
        }

        return scored.stream()
            .sorted((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")))
            .limit(Math.max(1, topK))
            .collect(Collectors.toList());
    }

    public Map<String, Object> rebuildEmbeddings(String mode, int limit) throws Exception {
        List<Item> items = itemRepo.selectAll();
        int processed = 0;
        int skipped = 0;

        for (Item item : items) {
            if (limit > 0 && processed >= limit) {
                break;
            }
            if (item.getItemPic() == null || item.getItemPic().isBlank()) {
                skipped++;
                continue;
            }
            ItemEmbedding existing = itemEmbeddingRepo.findByItemId(item.getItemId());
            if ("missing".equalsIgnoreCase(mode) && existing != null) {
                skipped++;
                continue;
            }
            String dataUrl = fetchImageAsDataUrl(item.getItemPic());
            String caption = openAiClient.generateCaption(dataUrl);
            List<Double> embedding = openAiClient.embedText(caption);
            String embeddingJson = objectMapper.writeValueAsString(embedding);
            ItemEmbedding record = new ItemEmbedding(item.getItemId(), caption, embeddingJson, null);
            itemEmbeddingRepo.upsert(record);
            processed++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("processed", processed);
        result.put("skipped", skipped);
        result.put("total", items.size());
        return result;
    }

    private String fetchImageAsDataUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        String contentType = conn.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "image/jpeg";
        }
        byte[] bytes;
        try (InputStream input = conn.getInputStream()) {
            bytes = input.readAllBytes();
        }
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + contentType + ";base64," + base64;
    }

    private double cosine(List<Double> a, List<Double> b) {
        double dot = 0.0;
        double na = 0.0;
        double nb = 0.0;
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
            double x = a.get(i);
            double y = b.get(i);
            dot += x * y;
            na += x * x;
            nb += y * y;
        }
        if (na == 0 || nb == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
