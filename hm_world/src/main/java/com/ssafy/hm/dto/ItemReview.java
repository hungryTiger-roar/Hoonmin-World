package com.ssafy.hm.dto;

public class ItemReview {
	private Integer itemReviewId;
	private String userId;
	private Integer itemId;
	private String itemReviewComment;

	public ItemReview() {
	}

	public ItemReview(Integer itemReviewId, String userId, Integer itemId, String itemReviewComment) {
		this.itemReviewId = itemReviewId;
		this.userId = userId;
		this.itemId = itemId;
		this.itemReviewComment = itemReviewComment;
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

	@Override
	public String toString() {
		return "ItemReview [itemReviewId=" + itemReviewId + ", userId=" + userId + ", itemId=" + itemId
				+ ", itemReviewComment=" + itemReviewComment + "]";
	}
}
