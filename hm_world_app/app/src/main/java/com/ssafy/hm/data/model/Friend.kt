package com.ssafy.hm.data.model

data class Friend(
    val id: Int,
    val userId: String,
    val friendId: String,
    val friendParty: Boolean = false
)

data class FriendWithDetails(
    val friend: Friend,
    val account: Account
)