package com.ssafy.hm.dto;

public class HomeImage {
	private Integer homeId;
	private String homeImage;

	public HomeImage() {}

	public HomeImage(Integer homeId, String homeImage) {
		this.homeId = homeId;
		this.homeImage = homeImage;
	}

	public Integer getHomeId() {
		return homeId;
	}

	public void setHomeId(Integer homeId) {
		this.homeId = homeId;
	}

	public String getHomeImage() {
		return homeImage;
	}

	public void setHomeImage(String homeImage) {
		this.homeImage = homeImage;
	}

	@Override
	public String toString() {
		return "HomeImage [homeId=" + homeId + ", homeImage=" + homeImage + "]";
	}	
	
}
