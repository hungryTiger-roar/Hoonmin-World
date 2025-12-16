package com.ssafy.hm.dto;

public class Account {
	private String userId;
	private String pw;
	private String name;
	private String phone;
	private String birth;
	private Integer attId;
	private Boolean ticket;

	public Account() {
	}

	public Account(String userId, String pw, String name, String phone, String birth, Integer attId, Boolean ticket) {
		this.userId = userId;
		this.pw = pw;
		this.name = name;
		this.phone = phone;
		this.birth = birth;
		this.attId = attId;
		this.ticket = ticket;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public Integer getAttId() {
		return attId;
	}

	public void setAttId(Integer attId) {
		this.attId = attId;
	}

	public Boolean getTicket() {
		return ticket;
	}

	public void setTicket(Boolean ticket) {
		this.ticket = ticket;
	}

	@Override
	public String toString() {
		return "Account [userId=" + userId + ", name=" + name + ", phone=" + phone + ", birth=" + birth + ", attId="
				+ attId + ", ticket=" + ticket + "]";
	}
}
