package com.ssafy.hm.data.model

data class Orders(
    val orderId: Int,
    val userId: String?,
    val orderStore: Int,
    val orderReceived: Boolean,
    val orderTime: String,
    val orderReceivedTime: String?
)

data class OrderDetail(
    val detailId: Int,
    val orderId: Int,
    val itemId: Int,
    val orderQuantity: Int,
    val detailReview: Boolean
)
