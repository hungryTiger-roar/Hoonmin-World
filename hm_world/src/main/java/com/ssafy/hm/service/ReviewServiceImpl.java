package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.AttractionReview;
import com.ssafy.hm.dto.ItemReview;
import com.ssafy.hm.repo.AttractionReviewRepo;
import com.ssafy.hm.repo.ItemReviewRepo;
import com.ssafy.hm.repo.OrderDetailRepo;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ItemReviewRepo itemReviewRepo;
	private final AttractionReviewRepo attractionReviewRepo;
	private final OrderDetailRepo orderDetailRepo;

	public ReviewServiceImpl(ItemReviewRepo itemReviewRepo, AttractionReviewRepo attractionReviewRepo,
			OrderDetailRepo orderDetailRepo) {
		this.itemReviewRepo = itemReviewRepo;
		this.attractionReviewRepo = attractionReviewRepo;
		this.orderDetailRepo = orderDetailRepo;
	}

	@Override
	@Transactional
	public boolean addItemReview(ItemReview review) {
		Integer detailId = orderDetailRepo.selectUnreviewedDetailId(review.getUserId(), review.getItemId());
		if (detailId == null) {
			return false;
		}
		if (itemReviewRepo.insert(review) != 1) {
			return false;
		}
		if (orderDetailRepo.markReviewed(detailId) != 1) {
			throw new IllegalStateException("Failed to mark order detail as reviewed");
		}
		return true;
	}

	@Override
	public boolean canAddItemReview(String userId, Integer itemId) {
		if (userId == null || itemId == null) {
			return false;
		}
		return orderDetailRepo.selectUnreviewedDetailId(userId, itemId) != null;
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
		if (!canAddAttractionReview(review.getUserId(), review.getAttId())) {
			return false;
		}
		return attractionReviewRepo.insert(review) == 1;
	}

	@Override
	public boolean canAddAttractionReview(String userId, Integer attId) {
		if (userId == null || attId == null) {
			return false;
		}
		int todayCount = attractionReviewRepo.selectTodayCountByUserAtt(userId, attId);
		return todayCount == 0;
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
