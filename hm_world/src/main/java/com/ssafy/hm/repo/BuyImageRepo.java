package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.BuyImage;

@Mapper
public interface BuyImageRepo {
	int insert(BuyImage image);
	List<BuyImage> selectAll();
	int delete(Integer buyId);
}
