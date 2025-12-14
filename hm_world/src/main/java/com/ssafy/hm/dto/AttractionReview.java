package com.ssafy.hm.dto;

public class AttractionReview {
	private Integer attReviewId;
	private Integer attId;
	private String userId;
	private String attReviewComment;

	public AttractionReview() {
	}

	public AttractionReview(Integer attReviewId, Integer attId, String userId, String attReviewComment) {
		this.attReviewId = attReviewId;
		this.attId = attId;
		this.userId = userId;
		this.attReviewComment = attReviewComment;
	}

	public Integer getAttReviewId() {
		return attReviewId;
	}

	public void setAttReviewId(Integer attReviewId) {
		this.attReviewId = attReviewId;
	}

	public Integer getAttId() {
		return attId;
	}

	public void setAttId(Integer attId) {
		this.attId = attId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAttReviewComment() {
		return attReviewComment;
	}

	public void setAttReviewComment(String attReviewComment) {
		this.attReviewComment = attReviewComment;
	}

	@Override
	public String toString() {
		return "AttractionReview [attReviewId=" + attReviewId + ", attId=" + attId + ", userId=" + userId
				+ ", attReviewComment=" + attReviewComment + "]";
	}
}
