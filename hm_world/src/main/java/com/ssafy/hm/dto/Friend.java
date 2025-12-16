package com.ssafy.hm.dto;

public class Friend {
	private Integer id;
	private String userId;
	private String friendId;
	private Boolean friendTicket;
	private Boolean friendParty;

	public Friend() {
	}

	public Friend(Integer id, String userId, String friendId, Boolean friendTicket, Boolean friendParty) {
		this.id = id;
		this.userId = userId;
		this.friendId = friendId;
		this.friendTicket = friendTicket;
		this.friendParty = friendParty;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public Boolean getFriendTicket() {
		return friendTicket;
	}

	public void setFriendTicket(Boolean friendTicket) {
		this.friendTicket = friendTicket;
	}

	public Boolean getFriendParty() {
		return friendParty;
	}

	public void setFriendParty(Boolean friendParty) {
		this.friendParty = friendParty;
	}

	@Override
	public String toString() {
		return "Friend [id=" + id + ", userId=" + userId + ", friendId=" + friendId + ", friendTicket=" + friendTicket
				+ ", friendParty=" + friendParty + "]";
	}

	
}
