package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.AttractionReview;
import com.ssafy.hm.dto.ItemReview;

public interface ReviewService {
	boolean addItemReview(ItemReview review);
	boolean canAddItemReview(String userId, Integer itemId);
	List<ItemReview> getItemReviews(Integer itemId);
	ItemReview getItemReview(Integer itemReviewId);
	boolean updateItemReview(ItemReview review);
	boolean deleteItemReview(Integer itemReviewId);

	boolean addAttractionReview(AttractionReview review);
	boolean canAddAttractionReview(String userId, Integer attId);
	List<AttractionReview> getAttractionReviews(Integer attId);
	AttractionReview getAttractionReview(Integer attReviewId);
	boolean updateAttractionReview(AttractionReview review);
	boolean deleteAttractionReview(Integer attReviewId);
}
