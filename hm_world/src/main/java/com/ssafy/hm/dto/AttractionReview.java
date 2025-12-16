package com.ssafy.hm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AttractionReview {
	private Integer attReviewId;
	private Integer attId;
	private String userId;
	private String attReviewComment;
	private Float attRating;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime attTime;

	public AttractionReview() {
	}

	public AttractionReview(Integer attReviewId, Integer attId, String userId, String attReviewComment, Float attRating, LocalDateTime attTime) {
		this.attReviewId = attReviewId;
		this.attId = attId;
		this.userId = userId;
		this.attReviewComment = attReviewComment;
		this.attRating = attRating;
		this.attTime = attTime;
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

	public Float getAttRating() {
		return attRating;
	}

	public void setAttRating(Float attRating) {
		this.attRating = attRating;
	}

	public LocalDateTime getAttTime() {
		return attTime;
	}

	public void setAttTime(LocalDateTime attTime) {
		this.attTime = attTime;
	}

	@Override
	public String toString() {
		return "AttractionReview [attReviewId=" + attReviewId + ", attId=" + attId + ", userId=" + userId
				+ ", attReviewComment=" + attReviewComment + ", attRating=" + attRating + ", attTime=" + attTime + "]";
	}

}
