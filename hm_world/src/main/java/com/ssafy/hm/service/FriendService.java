package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.Friend;

public interface FriendService {
	boolean addFriend(Friend friend);
	boolean removeFriend(Integer id);
	List<Friend> getFriends(String userId);
}
