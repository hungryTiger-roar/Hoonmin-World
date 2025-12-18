package com.ssafy.hm.data.model

data class ItemReview(
    val itemReviewId: Int,
    val userId: String?,
    val itemId: Int,
    val itemReviewComment: String?,
    val itemRating: Float = 5f,
    val itemTime: String
)
