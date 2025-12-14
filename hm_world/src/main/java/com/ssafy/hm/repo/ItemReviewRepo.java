package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.ItemReview;

@Mapper
public interface ItemReviewRepo {
	int insert(ItemReview review);
	List<ItemReview> selectByItem(Integer itemId);
}
