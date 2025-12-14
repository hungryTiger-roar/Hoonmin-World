package com.ssafy.hm.dto;

public class FriendLine {
	private Integer friendLineId;
	private Integer lineId;
	private String friendId;

	public FriendLine() {
	}

	public FriendLine(Integer friendLineId, Integer lineId, String friendId) {
		this.friendLineId = friendLineId;
		this.lineId = lineId;
		this.friendId = friendId;
	}

	public Integer getFriendLineId() {
		return friendLineId;
	}

	public void setFriendLineId(Integer friendLineId) {
		this.friendLineId = friendLineId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	@Override
	public String toString() {
		return "FriendLine [friendLineId=" + friendLineId + ", lineId=" + lineId + ", friendId=" + friendId + "]";
	}
}
