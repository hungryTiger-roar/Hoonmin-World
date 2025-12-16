package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.HomeBoard;

@Mapper
public interface HomeBoardRepo {

    List<HomeBoard> selectAll();

    HomeBoard selectById(int boardId);

    int insert(HomeBoard board);

    int update(HomeBoard board);

    int delete(int boardId);
}
