package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            _uiState.value = _uiState.value.copy(loading = true, error = null, registrationCompleted = false)
            try {
                val account = if (pw == null) repo.getAccount(id) else repo.login(id, pw)
                authStore.setUser(account.userId)
                _uiState.value = AuthUiState(account = account, autoLogin = auto, registrationCompleted = false)
            } catch (e: Exception) {
                val msg = if (e is HttpException && e.code() == 401) {
                    "아이디, 비밀번호를 확인해주세요."
                } else {
                    e.message ?: "로그인에 실패했습니다."
                }
                _uiState.value = AuthUiState(error = msg, registrationCompleted = false)
            }
        }
    }

    fun register(account: Account) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, registrationCompleted = false)
            try {
                val created = repo.register(account)
                authStore.setUser(created.userId)
                _uiState.value = AuthUiState(account = created, registrationCompleted = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "회원가입에 실패했습니다.", registrationCompleted = false)
            }
        }
    }

    suspend fun isIdAvailable(id: String): Boolean {
        return try {
            repo.getAccount(id)
            false // 이미 존재
        } catch (e: HttpException) {
            if (e.code() == 404) {
                true // 존재하지 않음
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
