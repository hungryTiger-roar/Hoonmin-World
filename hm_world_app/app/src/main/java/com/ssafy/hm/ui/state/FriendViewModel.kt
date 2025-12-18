package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.repository.FriendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            try {
                val friends = friendRepo.getFriends(user)
                val avail = friendRepo.getFriendsWithTicketAvailable(user)
                _state.value = _state.value.copy(friends = friends, availableFriends = avail)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun addFriend(friendId: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            try {
                friendRepo.addFriend(Friend(id = 0, userId = user, friendId = friendId, friendParty = false))
                loadFriends()
                _state.value = _state.value.copy(toast = "친구 추가")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun removeFriend(id: Int) {
        viewModelScope.launch {
            try {
                friendRepo.removeFriend(id)
                loadFriends()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearToast() {
        _state.value = _state.value.copy(toast = null)
    }
}
