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
	int updateParty(@Param("id") Integer id, @Param("friendParty") Boolean friendParty);
	int delete(Integer id);
}
