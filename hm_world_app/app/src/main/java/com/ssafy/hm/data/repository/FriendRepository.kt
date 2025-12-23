package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.model.FriendPartyRequest
import com.ssafy.hm.data.model.FriendWithDetails
import com.ssafy.hm.data.network.HmApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FriendRepository(private val api: HmApi) {
    suspend fun getFriends(userId: String): List<FriendWithDetails> = withContext(Dispatchers.IO) {
        val friends = api.getFriends(userId)
        friends.map { friend ->
            async {
                val account = api.getAccount(friend.friendId)
                FriendWithDetails(friend, account)
            }
        }.map { it.await() }
    }

    suspend fun getFriendsWithTicketAvailable(userId: String): List<FriendWithDetails> =
        withContext(Dispatchers.IO) {
            val friends = api.getFriendsWithTicketAvailable(userId)
            friends.map { friend ->
                async {
                    val account = api.getAccount(friend.friendId)
                    FriendWithDetails(friend, account)
                }
            }.map { it.await() }
        }

    suspend fun addFriend(friend: Friend): Friend = api.addFriend(friend)
    suspend fun removeFriend(id: Int) = api.removeFriend(id)
    suspend fun updateFriendParty(id: Int, friendParty: Boolean) {
        val request = FriendPartyRequest(friendParty)
        runCatching { api.updateFriendPartyPatch(id, request) }
            .getOrElse { api.updateFriendPartyPost(id, request) }
    }
}
