package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.HomeImage;

@Mapper
public interface HomeImageRepo {
	int insert(HomeImage image);
	List<HomeImage> selectAll();
	int delete(Integer homeId);
}
