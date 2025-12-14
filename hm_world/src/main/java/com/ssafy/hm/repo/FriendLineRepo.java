package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.FriendLine;

@Mapper
public interface FriendLineRepo {
	int insert(FriendLine friendLine);
	List<FriendLine> selectByLine(Integer lineId);
	int delete(Integer friendLineId);
}
