package com.ssafy.hm.dto;

public class FriendPartyRequest {
    private Boolean friendParty;

    public FriendPartyRequest() {
    }

    public FriendPartyRequest(Boolean friendParty) {
        this.friendParty = friendParty;
    }

    public Boolean getFriendParty() {
        return friendParty;
    }

    public void setFriendParty(Boolean friendParty) {
        this.friendParty = friendParty;
    }
}
