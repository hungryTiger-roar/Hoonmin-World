package com.ssafy.hm.data.model

data class OrderCreateRequest(
    val userId: String,
    val orderStore: Int,
    val details: List<OrderDetailPayload>
)

data class OrderDetailPayload(
    val itemId: Int,
    val orderQuantity: Int
)

data class AttractionLineCreateRequest(
    val attId: Int,
    val userIds: List<String>
)
