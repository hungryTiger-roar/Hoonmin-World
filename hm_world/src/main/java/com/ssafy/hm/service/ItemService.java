package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.Item;

public interface ItemService {
	boolean create(Item item);
	boolean update(Item item);
	boolean updateCategory(Item item);
	boolean remove(Integer itemId);

	Item get(Integer itemId);
	List<Item> getAll();
}
