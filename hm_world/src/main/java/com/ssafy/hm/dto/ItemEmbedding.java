package com.ssafy.hm.dto;

import java.time.LocalDateTime;

public class ItemEmbedding {
    private int itemId;
    private String imageCaption;
    private String embeddingJson;
    private LocalDateTime updatedAt;

    public ItemEmbedding() {
    }

    public ItemEmbedding(int itemId, String imageCaption, String embeddingJson, LocalDateTime updatedAt) {
        this.itemId = itemId;
        this.imageCaption = imageCaption;
        this.embeddingJson = embeddingJson;
        this.updatedAt = updatedAt;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public void setEmbeddingJson(String embeddingJson) {
        this.embeddingJson = embeddingJson;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
