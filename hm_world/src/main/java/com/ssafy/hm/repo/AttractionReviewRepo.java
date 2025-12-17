package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.AttractionReview;

@Mapper
public interface AttractionReviewRepo {
	int insert(AttractionReview review);
	List<AttractionReview> selectByAttraction(Integer attId);
	AttractionReview selectById(Integer attReviewId);
	int selectTodayCountByUserAtt(@Param("userId") String userId, @Param("attId") Integer attId);
	int updateCommentAndRating(AttractionReview review);
	int delete(Integer attReviewId);
}
