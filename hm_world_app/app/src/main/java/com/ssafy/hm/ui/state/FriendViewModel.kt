package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.repository.FriendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FriendState(
    val friends: List<Friend> = emptyList(),
    val availableFriends: List<Friend> = emptyList(),
    val error: String? = null,
    val toast: String? = null
)

class FriendViewModel(
    private val friendRepo: FriendRepository
) : ViewModel() {
    private var currentUser: String? = null

    private val _state = MutableStateFlow(FriendState())
    val state: StateFlow<FriendState> = _state

    fun setUser(userId: String) {
        currentUser = userId
        loadFriends()
    }

    fun loadFriends() {
        val user = currentUser ?: return
        viewModelScope.launch {
            runCatching {
                val friends = friendRepo.getFriends(user)
                val avail = friendRepo.getFriendsWithTicketAvailable(user)
                Pair(friends, avail)
            }.onSuccess { (friends, avail) ->
                _state.update { it.copy(friends = friends, availableFriends = avail) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun addFriend(friendId: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            runCatching {
                friendRepo.addFriend(Friend(id = 0, userId = user, friendId = friendId, friendParty = false))
            }.onSuccess {
                loadFriends()
                _state.update { it.copy(toast = "친구가 추가되었습니다.") }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeFriend(id: Int) {
        viewModelScope.launch {
            runCatching { friendRepo.removeFriend(id) }
                .onSuccess { loadFriends() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
