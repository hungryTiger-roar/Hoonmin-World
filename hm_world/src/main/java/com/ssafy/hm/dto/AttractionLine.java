package com.ssafy.hm.dto;

public class AttractionLine {
	private Integer lineId;
	private Integer attId;
	private String userId;

	public AttractionLine() {
	}

	public AttractionLine(Integer lineId, Integer attId, String userId) {
		this.lineId = lineId;
		this.attId = attId;
		this.userId = userId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
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

	@Override
	public String toString() {
		return "AttractionLine [lineId=" + lineId + ", attId=" + attId + ", userId=" + userId + "]";
	}
}
