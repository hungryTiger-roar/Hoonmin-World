package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.network.HmApi

class FriendRepository(private val api: HmApi) {
    suspend fun getFriends(userId: String): List<Friend> = api.getFriends(userId)
    suspend fun getFriendsWithTicketAvailable(userId: String): List<Friend> = api.getFriendsWithTicketAvailable(userId)
    suspend fun addFriend(friend: Friend): Friend = api.addFriend(friend)
    suspend fun removeFriend(id: Int) = api.removeFriend(id)
}
