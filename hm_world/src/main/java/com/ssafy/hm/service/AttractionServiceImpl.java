package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.Attraction;
import com.ssafy.hm.repo.AttractionRepo;

@Service
public class AttractionServiceImpl implements AttractionService {

	private final AttractionRepo attractionRepo;

	public AttractionServiceImpl(AttractionRepo attractionRepo) {
		this.attractionRepo = attractionRepo;
	}

	@Override
	@Transactional
	public boolean create(Attraction attraction) {
		return attractionRepo.insert(attraction) == 1;
	}

	@Override
	@Transactional
	public boolean update(Attraction attraction) {
		return attractionRepo.update(attraction) == 1;
	}

	@Override
	@Transactional
	public boolean updateAble(Attraction attraction) {
		return attractionRepo.updateAble(attraction) == 1;
	}
	
	@Override
	@Transactional
	public boolean updateCategory(Attraction attraction) {   
		return attractionRepo.updateCategory(attraction) == 1;
	}
	
	@Override
	@Transactional
	public boolean updateTotal(Attraction attraction) {   
		return attractionRepo.updateTotal(attraction) == 1;
	}
	
	@Override
	@Transactional
	public boolean remove(Integer attId) {
		return attractionRepo.delete(attId) == 1;
	}

	@Override
	public Attraction get(Integer attId) {
		return attractionRepo.selectById(attId);
	}

	@Override
	public List<Attraction> getAll() {
		return attractionRepo.selectAll();
	}
}
