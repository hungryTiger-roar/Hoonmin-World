package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.AttractionLine
import com.ssafy.hm.data.model.AttractionLineCreateRequest
import com.ssafy.hm.data.model.AttractionLineMember
import com.ssafy.hm.data.network.HmApi

class LineRepository(private val api: HmApi) {
    suspend fun createLine(attId: Int, req: AttractionLineCreateRequest): Int = api.createLine(attId, req)
    suspend fun getLinesByAttraction(attId: Int): List<AttractionLine> = api.getLinesByAttraction(attId)
    suspend fun getLineMembers(lineId: Int): List<AttractionLineMember> = api.getLineMembers(lineId)
    suspend fun deleteLineMember(lineId: Int, userId: String) = api.deleteLineMember(lineId, userId)
    suspend fun deleteLine(lineId: Int) = api.deleteLine(lineId)
}
