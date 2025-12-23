package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.model.FriendWithDetails
import com.ssafy.hm.data.repository.AccountRepository
import com.ssafy.hm.data.repository.FriendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FriendState(
    val friends: List<FriendWithDetails> = emptyList(),
    val availableFriends: List<FriendWithDetails> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val error: String? = null,
    val toast: String? = null
)

class FriendViewModel(
    private val friendRepo: FriendRepository,
    private val accountRepo: AccountRepository
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
                val accounts = accountRepo.getAccounts()
                Triple(friends, avail, accounts)
            }.onSuccess { (friends, avail, accounts) ->
                _state.update { it.copy(friends = friends, availableFriends = avail, accounts = accounts) }
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
                .onSuccess {
                    loadFriends()
                    _state.update { it.copy(toast = "친구가 삭제되었습니다") }
                }
                .onFailure { _ ->
                    _state.update { it.copy(error = "친구 삭제에 실패했습니다.") }
                }
        }
    }

    fun updateFriendParty(id: Int, party: Boolean) {
        _state.update { state ->
            state.copy(
                friends = state.friends.map { friend ->
                    if (friend.friend.id == id) friend.copy(friend = friend.friend.copy(friendParty = party)) else friend
                },
                availableFriends = state.availableFriends.map { friend ->
                    if (friend.friend.id == id) friend.copy(friend = friend.friend.copy(friendParty = party)) else friend
                }
            )
        }
        viewModelScope.launch {
            runCatching { friendRepo.updateFriendParty(id, party) }
                .onSuccess { loadFriends() }
                .onFailure { _state.update { it.copy(error = "파티 설정에 실패했습니다.") } }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
