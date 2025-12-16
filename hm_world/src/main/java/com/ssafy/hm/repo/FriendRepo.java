package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.Friend;

@Mapper
public interface FriendRepo {
	int insert(Friend friend);
	List<Friend> selectByUser(String userId);
	List<Friend> selectByUserTicket(String userId);
	int delete(Integer id);
}
