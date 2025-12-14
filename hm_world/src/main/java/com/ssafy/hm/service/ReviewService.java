package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.AttractionReview;
import com.ssafy.hm.dto.ItemReview;

public interface ReviewService {
	boolean addItemReview(ItemReview review);
	List<ItemReview> getItemReviews(Integer itemId);

	boolean addAttractionReview(AttractionReview review);
	List<AttractionReview> getAttractionReviews(Integer attId);
}
