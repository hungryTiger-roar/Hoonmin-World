package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.Attraction;

public interface AttractionService {
	boolean create(Attraction attraction);
	boolean update(Attraction attraction);
	boolean updateAble(Attraction attraction);
	boolean updateCategory(Attraction attraction); 
	boolean updateTotal(Attraction attraction); 
	boolean remove(Integer attId);

	Attraction get(Integer attId);
	List<Attraction> getAll();
}
