package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeBoardWriteRequest
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.repository.BoardRepository
import com.ssafy.hm.data.repository.HomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class HomeState(
    val loading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
    val homeImages: List<HomeImage> = emptyList(),
    val buyImages: List<BuyImage> = emptyList(),
    val boards: List<HomeBoard> = emptyList()
)

class HomeViewModel(
    private val homeRepo: HomeRepository,
    private val boardRepo: BoardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
    private val refreshMutex = Mutex()
    private var monitorJob: Job? = null

    fun refresh() {
        viewModelScope.launch { loadHomeData(showLoading = true) }
    }

    fun createBoard(title: String, content: String) {
        viewModelScope.launch {
            runCatching {
                val req = HomeBoardWriteRequest(boardTitle = title, boardContent = content)
                boardRepo.createBoard(req)
            }.onSuccess {
                _state.update { it.copy(toast = "Board created") }
                refresh()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateBoard(boardId: Int, title: String, content: String) {
        viewModelScope.launch {
            runCatching {
                val req = HomeBoardWriteRequest(boardTitle = title, boardContent = content)
                boardRepo.updateBoard(boardId, req)
            }.onSuccess {
                _state.update { it.copy(toast = "Board updated") }
                refresh()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteBoard(boardId: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.update { it.copy(boards = it.boards.filterNot { b -> b.boardId == boardId }) }
            runCatching { boardRepo.deleteBoard(boardId) }
                .onSuccess {
                    _state.update { it.copy(toast = "Board deleted") }
                    refresh()
                }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun deleteHomeImage(id: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.value = prev.copy(homeImages = prev.homeImages.filterNot { it.homeId == id })
            runCatching { homeRepo.deleteHomeImage(id) }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun deleteBuyImage(id: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.value = prev.copy(buyImages = prev.buyImages.filterNot { it.buyId == id })
            runCatching { homeRepo.deleteBuyImage(id) }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun startMonitoring(intervalMs: Long = 100L, initialDelayMs: Long = intervalMs) {
        if (monitorJob?.isActive == true) return
        monitorJob = viewModelScope.launch {
            delay(initialDelayMs)
            while (true) {
                loadHomeData(showLoading = false)
                delay(intervalMs)
            }
        }
    }

    fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
    }

    fun clearToast() {
        _state.value = _state.value.copy(toast = null)
    }

    private suspend fun loadHomeData(showLoading: Boolean) {
        refreshMutex.withLock {
            if (showLoading) {
                _state.update { it.copy(loading = true, error = null) }
            }
            runCatching {
                val homeImages = homeRepo.getHomeImages()
                val buyImages = homeRepo.getBuyImages()
                val boards = boardRepo.getBoards()
                Triple(homeImages, buyImages, boards)
            }.onSuccess { (homeImages, buyImages, boards) ->
                _state.update {
                    it.copy(
                        loading = false,
                        homeImages = homeImages,
                        buyImages = buyImages,
                        boards = boards,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
