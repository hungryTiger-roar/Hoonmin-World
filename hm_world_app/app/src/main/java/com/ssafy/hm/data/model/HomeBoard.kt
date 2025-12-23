package com.ssafy.hm.data.model

import com.google.gson.annotations.SerializedName

data class HomeBoard(
    val boardId: Int,
    val boardTitle: String?,
    val boardContent: String?,
    @SerializedName(value = "boardTime", alternate = ["board_date"])
    val boardDate: String?
)
