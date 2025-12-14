package com.ssafy.hm.dto;

public class Orders {
	private Integer orderId;
	private String userId;

	public Orders() {
	}

	public Orders(Integer orderId, String userId) {
		this.orderId = orderId;
		this.userId = userId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", userId=" + userId + "]";
	}
}
