package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.Item;
import com.ssafy.hm.repo.ItemRepo;

@Service
public class ItemServiceImpl implements ItemService {

	private final ItemRepo itemRepo;

	public ItemServiceImpl(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@Override
	@Transactional
	public boolean create(Item item) {
		return itemRepo.insert(item) == 1;
	}

	@Override
	@Transactional
	public boolean update(Item item) {
		return itemRepo.update(item) == 1;
	}
	
	@Override
	@Transactional
	public boolean updateCategory(Item item) {
		return itemRepo.updateCategory(item) == 1;
	}

	@Override
	@Transactional
	public boolean remove(Integer itemId) {
		return itemRepo.delete(itemId) == 1;
	}

	@Override
	public Item get(Integer itemId) {
		return itemRepo.selectById(itemId);
	}

	@Override
	public List<Item> getAll() {
		return itemRepo.selectAll();
	}
}
