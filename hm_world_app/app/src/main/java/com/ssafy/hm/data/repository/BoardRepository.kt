package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeBoardWriteRequest
import com.ssafy.hm.data.network.HmApi

class BoardRepository(private val api: HmApi) {
    suspend fun getBoards(): List<HomeBoard> = api.getBoards()
    suspend fun getBoard(id: Int): HomeBoard = api.getBoard(id)
    suspend fun createBoard(board: HomeBoardWriteRequest) = api.createBoard(board)
    suspend fun updateBoard(id: Int, board: HomeBoardWriteRequest) = api.updateBoard(id, board)
    suspend fun deleteBoard(id: Int) = api.deleteBoard(id)
}
