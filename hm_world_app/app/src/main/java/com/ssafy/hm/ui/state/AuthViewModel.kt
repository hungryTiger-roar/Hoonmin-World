package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val account: Account? = null,
    val autoLogin: Boolean = false
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
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val account = if (pw == null) repo.getAccount(id) else repo.login(id, pw)
                authStore.setUser(account.userId)
                _uiState.value = AuthUiState(account = account, autoLogin = auto)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "로그인 실패")
            }
        }
    }

    fun register(account: Account) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val created = repo.register(account)
                authStore.setUser(created.userId)
                _uiState.value = AuthUiState(account = created)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "회원가입 실패")
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
