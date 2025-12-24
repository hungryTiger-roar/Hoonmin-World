package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.Friend;

@Mapper
public interface FriendRepo {
	int insert(Friend friend);
	List<Friend> selectByUser(String userId);
	List<Friend> selectByUserTicket(String userId);
	List<Friend> selectByUserTicketNoAtt(String userId);
	List<Friend> selectByUserTicketNotPurchased(String userId);
	int updateParty(@Param("id") Integer id, @Param("friendParty") Boolean friendParty);
	int updatePartyByIds(@Param("userId") String userId, @Param("friendId") String friendId, @Param("friendParty") Boolean friendParty);
	int delete(Integer id);
}
