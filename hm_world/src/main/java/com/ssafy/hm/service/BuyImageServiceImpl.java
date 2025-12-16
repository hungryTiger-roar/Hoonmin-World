package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.BuyImage;
import com.ssafy.hm.repo.BuyImageRepo;

@Service
public class BuyImageServiceImpl implements BuyImageService {

	private final BuyImageRepo buyImageRepo;

	public BuyImageServiceImpl(BuyImageRepo buyImageRepo) {
		this.buyImageRepo = buyImageRepo;
	}

	@Override
	@Transactional
	public boolean create(BuyImage image) {
		return buyImageRepo.insert(image) == 1;
	}

	@Override
	public List<BuyImage> getAll() {
		return buyImageRepo.selectAll();
	}

	@Override
	@Transactional
	public boolean remove(Integer buyId) {
		return buyImageRepo.delete(buyId) == 1;
	}
}
