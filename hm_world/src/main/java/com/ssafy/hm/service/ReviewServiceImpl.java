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
	public ItemReview getItemReview(Integer itemReviewId) {
		return itemReviewRepo.selectById(itemReviewId);
	}

	@Override
	@Transactional
	public boolean updateItemReview(ItemReview review) {
		return itemReviewRepo.updateCommentAndRating(review) == 1;
	}

	@Override
	@Transactional
	public boolean deleteItemReview(Integer itemReviewId) {
		return itemReviewRepo.delete(itemReviewId) == 1;
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

	@Override
	public AttractionReview getAttractionReview(Integer attReviewId) {
		return attractionReviewRepo.selectById(attReviewId);
	}

	@Override
	@Transactional
	public boolean updateAttractionReview(AttractionReview review) {
		return attractionReviewRepo.updateCommentAndRating(review) == 1;
	}

	@Override
	@Transactional
	public boolean deleteAttractionReview(Integer attReviewId) {
		return attractionReviewRepo.delete(attReviewId) == 1;
	}
}
