package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.AttractionReview;

@Mapper
public interface AttractionReviewRepo {
	int insert(AttractionReview review);
	List<AttractionReview> selectByAttraction(Integer attId);
}
