package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.AttractionReview;
import com.ssafy.hm.dto.ItemReview;
import com.ssafy.hm.repo.AttractionReviewRepo;
import com.ssafy.hm.repo.ItemReviewRepo;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ItemReviewRepo itemReviewRepo;
	private final AttractionReviewRepo attractionReviewRepo;

	public ReviewServiceImpl(ItemReviewRepo itemReviewRepo, AttractionReviewRepo attractionReviewRepo) {
		this.itemReviewRepo = itemReviewRepo;
		this.attractionReviewRepo = attractionReviewRepo;
	}

	@Override
	@Transactional
	public boolean addItemReview(ItemReview review) {
		return itemReviewRepo.insert(review) == 1;
	}

	@Override
	public List<ItemReview> getItemReviews(Integer itemId) {
		return itemReviewRepo.selectByItem(itemId);
	}

	@Override
	@Transactional
	public boolean addAttractionReview(AttractionReview review) {
		return attractionReviewRepo.insert(review) == 1;
	}

	@Override
	public List<AttractionReview> getAttractionReviews(Integer attId) {
		return attractionReviewRepo.selectByAttraction(attId);
	}
}
