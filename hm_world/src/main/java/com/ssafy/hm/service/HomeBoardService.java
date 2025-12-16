package com.ssafy.hm.service;

import java.util.List;
import com.ssafy.hm.dto.HomeBoard;

public interface HomeBoardService {

    List<HomeBoard> getBoards();

    HomeBoard getBoard(int boardId);

    boolean writeBoard(HomeBoard board);
    
    boolean updateBoard(HomeBoard board);

    boolean removeBoard(int boardId);
}
