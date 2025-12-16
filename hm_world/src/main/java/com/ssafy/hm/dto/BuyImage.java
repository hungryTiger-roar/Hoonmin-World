package com.ssafy.hm.dto;

public class BuyImage {
	private Integer buyId;
	private String buyImage;

	public BuyImage() {}

	public BuyImage(Integer buyId, String buyImage) {
		this.buyId = buyId;
		this.buyImage = buyImage;
	}

	public Integer getBuyId() {
		return buyId;
	}

	public void setBuyId(Integer buyId) {
		this.buyId = buyId;
	}

	public String getBuyImage() {
		return buyImage;
	}

	public void setBuyImage(String buyImage) {
		this.buyImage = buyImage;
	}

	@Override
	public String toString() {
		return "BuyImage [buyId=" + buyId + ", buyImage=" + buyImage + "]";
	}	
}
