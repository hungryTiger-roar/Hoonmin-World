package com.ssafy.hm.data.model

data class AttractionLine(
    val lineId: Int,
    val attId: Int
)

data class AttractionLineMember(
    val id: Int,
    val lineId: Int,
    val userId: String
)
