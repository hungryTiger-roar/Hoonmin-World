package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val account: Account? = null,
    val autoLogin: Boolean = false,
    val registrationCompleted: Boolean = false
)

class AuthViewModel(
    private val repo: AccountRepository,
    private val authStore: AuthStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun tryAutoLogin() {
        viewModelScope.launch {
            authStore.userIdFlow.collect { id ->
                if (id != null) {
                    login(id, null, auto = true)
                }
            }
        }
    }

    fun login(id: String, pw: String?, auto: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, registrationCompleted = false) }
            runCatching {
                if (pw == null) repo.getAccount(id) else repo.login(id, pw)
            }.onSuccess { account ->
                authStore.setUser(account.userId)
                _uiState.value = AuthUiState(account = account, autoLogin = auto, registrationCompleted = false)
            }.onFailure { e ->
                val msg = if (e is HttpException && e.code() == 401) {
                    "?勳澊?? 牍勲?氩堩樃毳??曥澑?挫＜?胳殧."
                } else {
                    e.message ?: "搿滉犯?胳棎 ?ろ尐?堨姷?堧嫟."
                }
                _uiState.value = AuthUiState(error = msg, registrationCompleted = false)
            }
        }
    }

    fun register(account: Account) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, registrationCompleted = false) }
            runCatching {
                repo.register(account)
            }.onSuccess { created ->
                authStore.setUser(created.userId)
                _uiState.value = AuthUiState(account = created, registrationCompleted = true)
            }.onFailure { e ->
                _uiState.value = AuthUiState(
                    error = e.message ?: "?岇洂臧€?呾棎 ?ろ尐?堨姷?堧嫟.",
                    registrationCompleted = false
                )
            }
        }
    }

    suspend fun isIdAvailable(id: String): Boolean {
        return try {
            repo.getAccount(id)
            false // ?措? 臁挫灛
        } catch (e: HttpException) {
            if (e.code() == 404) {
                true // 臁挫灛?橃? ?婌潓
            } else {
                throw e
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authStore.setUser(null)
            _uiState.value = AuthUiState()
        }
    }
}
