package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.Item;

@Mapper
public interface ItemRepo {
	int insert(Item item);
	int update(Item item);
	int delete(Integer itemId);

	Item selectById(Integer itemId);
	List<Item> selectAll();
}
