package com.ssafy.hm.dto;

public class Attraction {
	private Integer attId;
	private String attName;
	private String attPic;
	private Integer attCapacity;
	private String attComment;
	private Boolean attAble;

	public Attraction() {
	}

	public Attraction(Integer attId, String attName, String attPic, Integer attCapacity, String attComment, Boolean attAble) {
		this.attId = attId;
		this.attName = attName;
		this.attPic = attPic;
		this.attCapacity = attCapacity;
		this.attComment = attComment;
		this.attAble = attAble;
	}

	public Integer getAttId() {
		return attId;
	}

	public void setAttId(Integer attId) {
		this.attId = attId;
	}

	public String getAttName() {
		return attName;
	}

	public void setAttName(String attName) {
		this.attName = attName;
	}

	public String getAttPic() {
		return attPic;
	}

	public void setAttPic(String attPic) {
		this.attPic = attPic;
	}

	public Integer getAttCapacity() {
		return attCapacity;
	}

	public void setAttCapacity(Integer attCapacity) {
		this.attCapacity = attCapacity;
	}

	public String getAttComment() {
		return attComment;
	}

	public void setAttComment(String attComment) {
		this.attComment = attComment;
	}

	public Boolean getAttAble() {
		return attAble;
	}

	public void setAttAble(Boolean attAble) {
		this.attAble = attAble;
	}

	@Override
	public String toString() {
		return "Attraction [attId=" + attId + ", attName=" + attName + ", attPic=" + attPic + ", attCapacity="
				+ attCapacity + ", attComment=" + attComment + ", attAble=" + attAble + "]";
	}

}
