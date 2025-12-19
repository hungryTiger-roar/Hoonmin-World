package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.repository.BoardRepository
import com.ssafy.hm.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val loading: Boolean = false,
    val error: String? = null,
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

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val homeImages = homeRepo.getHomeImages()
                val buyImages = homeRepo.getBuyImages()
                val boards = boardRepo.getBoards()
                _state.value = HomeState(
                    loading = false,
                    homeImages = homeImages,
                    buyImages = buyImages,
                    boards = boards
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun createBoard(title: String, content: String) {
        viewModelScope.launch {
            try {
                boardRepo.createBoard(
                    HomeBoard(boardId = 0, boardTitle = title, boardContent = content, boardDate = "")
                )
                refresh()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteBoard(id: Int) {
        viewModelScope.launch {
            try {
                boardRepo.deleteBoard(id)
                refresh()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun getBoardById(id: Int): HomeBoard? {
        return _state.value.boards.find { it.boardId == id }
    }
}
