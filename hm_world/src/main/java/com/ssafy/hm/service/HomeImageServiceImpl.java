package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.HomeImage;
import com.ssafy.hm.repo.HomeImageRepo;

@Service
public class HomeImageServiceImpl implements HomeImageService {

	private final HomeImageRepo homeImageRepo;

	public HomeImageServiceImpl(HomeImageRepo homeImageRepo) {
		this.homeImageRepo = homeImageRepo;
	}

	@Override
	@Transactional
	public boolean create(HomeImage image) {
		return homeImageRepo.insert(image) == 1;
	}

	@Override
	public List<HomeImage> getAll() {
		return homeImageRepo.selectAll();
	}

	@Override
	@Transactional
	public boolean remove(Integer homeId) {
		return homeImageRepo.delete(homeId) == 1;
	}
}
