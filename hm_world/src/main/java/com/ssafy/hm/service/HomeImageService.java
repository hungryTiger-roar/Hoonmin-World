package com.ssafy.hm.service;

import java.util.List;
import com.ssafy.hm.dto.HomeImage;

public interface HomeImageService {
	boolean create(HomeImage image);
	List<HomeImage> getAll();
	boolean remove(Integer homeId);
}
