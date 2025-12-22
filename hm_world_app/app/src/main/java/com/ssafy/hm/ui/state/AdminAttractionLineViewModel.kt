package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.AttractionLine
import com.ssafy.hm.data.repository.AccountRepository
import com.ssafy.hm.data.repository.LineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminAttractionLineState(
    val lines: List<AttractionLine> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AdminAttractionLineViewModel(
    private val lineRepo: LineRepository,
    private val accountRepo: AccountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AdminAttractionLineState())
    val state: StateFlow<AdminAttractionLineState> = _state

    private var currentAttId: Int? = null

    fun load(attId: Int) {
        currentAttId = attId
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching {
                val lines = lineRepo.getLinesByAttraction(attId)
                val accounts = accountRepo.getAccounts()
                Pair(lines, accounts)
            }.onSuccess { (lines, accounts) ->
                _state.update { it.copy(lines = lines, accounts = accounts, loading = false, error = null) }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        currentAttId?.let { load(it) }
    }

    fun deleteLine(lineId: Int) {
        viewModelScope.launch {
            runCatching { lineRepo.deleteLine(lineId) }
                .onSuccess { refresh() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun deleteMember(lineId: Int, userId: String) {
        viewModelScope.launch {
            runCatching { lineRepo.deleteLineMember(lineId, userId) }
                .onSuccess { refresh() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
