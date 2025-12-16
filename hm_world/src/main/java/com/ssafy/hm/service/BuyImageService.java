package com.ssafy.hm.service;

import java.util.List;
import com.ssafy.hm.dto.BuyImage;

public interface BuyImageService {
	boolean create(BuyImage image);
	List<BuyImage> getAll();
	boolean remove(Integer buyId);
}
