package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.ItemEmbedding;

@Mapper
public interface ItemEmbeddingRepo {
    ItemEmbedding findByItemId(int itemId);
    List<ItemEmbedding> findAll();
    int upsert(ItemEmbedding embedding);
}
