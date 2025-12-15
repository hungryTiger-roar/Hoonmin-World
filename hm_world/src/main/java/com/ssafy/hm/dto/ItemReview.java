package com.ssafy.hm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ItemReview {
	private Integer itemReviewId;
	private String userId;
	private Integer itemId;
	private String itemReviewComment;
	private Float itemRating;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime itemTime;

	public ItemReview() {
	}

	public ItemReview(Integer itemReviewId, String userId, Integer itemId, String itemReviewComment, Float itemRating,
			LocalDateTime itemTime) {
		this.itemReviewId = itemReviewId;
		this.userId = userId;
		this.itemId = itemId;
		this.itemReviewComment = itemReviewComment;
		this.itemRating = itemRating;
		this.itemTime = itemTime;
	}

	public Integer getItemReviewId() {
		return itemReviewId;
	}

	public void setItemReviewId(Integer itemReviewId) {
		this.itemReviewId = itemReviewId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemReviewComment() {
		return itemReviewComment;
	}

	public void setItemReviewComment(String itemReviewComment) {
		this.itemReviewComment = itemReviewComment;
	}

	public Float getItemRating() {
		return itemRating;
	}

	public void setItemRating(Float itemRating) {
		this.itemRating = itemRating;
	}

	public LocalDateTime getItemTime() {
		return itemTime;
	}

	public void setItemTime(LocalDateTime itemTime) {
		this.itemTime = itemTime;
	}

	@Override
	public String toString() {
		return "ItemReview [itemReviewId=" + itemReviewId + ", userId=" + userId + ", itemId=" + itemId
				+ ", itemReviewComment=" + itemReviewComment + ", itemRating=" + itemRating + ", itemTime=" + itemTime
				+ "]";
	}

	
}
