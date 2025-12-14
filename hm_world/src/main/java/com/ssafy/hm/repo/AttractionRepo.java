package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.Attraction;

@Mapper
public interface AttractionRepo {
	int insert(Attraction attraction);
	int update(Attraction attraction);
	int delete(Integer attId);

	Attraction selectById(Integer attId);
	List<Attraction> selectAll();
}
