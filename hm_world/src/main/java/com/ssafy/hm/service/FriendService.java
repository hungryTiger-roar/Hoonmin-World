package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.Friend;

public interface FriendService {
	boolean addFriend(Friend friend);
	boolean removeFriend(Integer id);
	boolean updateFriendParty(Integer id, Boolean friendParty);
	boolean updateFriendPartyByIds(String userId, String friendId, Boolean friendParty);
	List<Friend> getFriends(String userId);
	List<Friend> getFriendsTicket(String userId);
	List<Friend> getFriendsTicketNoAtt(String userId);
}
