package com.ssafy.hm.data.model

data class Attraction(
    val attId: Int,
    val attName: String,
    val attPic: String?,
    val attCapacity: Int,
    val attComment: String?,
    val attAble: Boolean = true,
    val attCategory: String? = null,
    val attTotal: Int = 0
)
