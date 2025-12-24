package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.Friend;
import com.ssafy.hm.repo.FriendRepo;

@Service
public class FriendServiceImpl implements FriendService {

	private final FriendRepo friendRepo;

	public FriendServiceImpl(FriendRepo friendRepo) {
		this.friendRepo = friendRepo;
	}

	@Override
	@Transactional
	public boolean addFriend(Friend friend) {
		return friendRepo.insert(friend) == 1;
	}

	@Override
	@Transactional
	public boolean removeFriend(Integer id) {
		return friendRepo.delete(id) == 1;
	}

	@Override
	@Transactional
	public boolean updateFriendParty(Integer id, Boolean friendParty) {
		if (id == null || friendParty == null) {
			return false;
		}
		return friendRepo.updateParty(id, friendParty) == 1;
	}

	@Override
	@Transactional
	public boolean updateFriendPartyByIds(String userId, String friendId, Boolean friendParty) {
		if (userId == null || userId.isBlank() || friendId == null || friendId.isBlank() || friendParty == null) {
			return false;
		}
		return friendRepo.updatePartyByIds(userId, friendId, friendParty) == 1;
	}

	@Override
	public List<Friend> getFriends(String userId) {
		return friendRepo.selectByUser(userId);
	}
	

	@Override
	public List<Friend> getFriendsTicket(String userId) {
		return friendRepo.selectByUserTicket(userId);
	}

	@Override
	public List<Friend> getFriendsTicketNoAtt(String userId) {
		return friendRepo.selectByUserTicketNoAtt(userId);
	}

	@Override
	public List<Friend> getFriendsTicketNotPurchased(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
}
