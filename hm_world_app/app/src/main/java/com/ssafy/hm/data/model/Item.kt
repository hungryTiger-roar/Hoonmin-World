package com.ssafy.hm.data.model

data class Item(
    val itemId: Int,
    val itemName: String,
    val itemPrice: Int,
    val itemCount: Int,
    val itemPic: String?,
    val itemComment: String?,
    val itemCategory: String?,
    val itemTime: String
)
