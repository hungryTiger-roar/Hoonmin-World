package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.AttractionLine;

@Mapper
public interface AttractionLineRepo {
	int insert(AttractionLine line);
	List<AttractionLine> selectByAttraction(Integer attId);
	int delete(Integer lineId);
}
