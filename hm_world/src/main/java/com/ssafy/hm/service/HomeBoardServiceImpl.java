package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.hm.dto.HomeBoard;
import com.ssafy.hm.repo.HomeBoardRepo;

@Service
public class HomeBoardServiceImpl implements HomeBoardService {

    private final HomeBoardRepo homeBoardRepo;

    public HomeBoardServiceImpl(HomeBoardRepo homeBoardRepo) {
        this.homeBoardRepo = homeBoardRepo;
    }

    @Override
    public List<HomeBoard> getBoards() {
        return homeBoardRepo.selectAll();
    }

    @Override
    public HomeBoard getBoard(int boardId) {
        return homeBoardRepo.selectById(boardId);
    }

    @Override
    public boolean writeBoard(HomeBoard board) {
        return homeBoardRepo.insert(board) == 1;
    }

    @Override
    public boolean removeBoard(int boardId) {
        return homeBoardRepo.delete(boardId) == 1;
    }
}
