package com.ssafy.hm.dto;

public class OrderDetail {
	private Integer detailId;
	private Integer orderId;
	private Integer itemId;
	private Integer orderQuantity;

	public OrderDetail() {
	}

	public OrderDetail(Integer detailId, Integer orderId, Integer itemId, Integer orderQuantity) {
		this.detailId = detailId;
		this.orderId = orderId;
		this.itemId = itemId;
		this.orderQuantity = orderQuantity;
	}

	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	@Override
	public String toString() {
		return "OrderDetail [detailId=" + detailId + ", orderId=" + orderId + ", itemId=" + itemId
				+ ", orderQuantity=" + orderQuantity + "]";
	}
}
