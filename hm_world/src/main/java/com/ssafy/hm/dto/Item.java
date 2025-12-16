package com.ssafy.hm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Item {
	private Integer itemId;
	private String itemName;
	private Integer itemPrice;
	private Integer itemCount;
	private String itemPic;
	private String itemComment;
	private String itemCategory;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime itemTime;

	public Item() {
	}

	public Item(Integer itemId, String itemName, Integer itemPrice, Integer itemCount, String itemPic,
			String itemComment, String itemCategory, LocalDateTime itemTime) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.itemCount = itemCount;
		this.itemPic = itemPic;
		this.itemComment = itemComment;
		this.itemCategory = itemCategory;
		this.itemTime = itemTime;
	}

	public LocalDateTime getItemTime() {
		return itemTime;
	}

	public void setItemTime(LocalDateTime itemTime) {
		this.itemTime = itemTime;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Integer itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public String getItemPic() {
		return itemPic;
	}

	public void setItemPic(String itemPic) {
		this.itemPic = itemPic;
	}

	public String getItemComment() {
		return itemComment;
	}

	public void setItemComment(String itemComment) {
		this.itemComment = itemComment;
	}

	@Override
	public String toString() {
		return "Item [itemId=" + itemId + ", itemName=" + itemName + ", itemPrice=" + itemPrice + ", itemCount="
				+ itemCount + ", itemPic=" + itemPic + ", itemComment=" + itemComment + ", itemCategory=" + itemCategory
				+ ", itemTime=" + itemTime + "]";
	}
}
