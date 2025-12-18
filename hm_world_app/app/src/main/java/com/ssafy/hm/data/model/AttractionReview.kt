package com.ssafy.hm.data.model

data class AttractionReview(
    val attReviewId: Int,
    val attId: Int,
    val userId: String?,
    val attReviewComment: String?,
    val attRating: Float = 5f,
    val attTime: String
)
