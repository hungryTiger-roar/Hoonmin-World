package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.AttractionLineCreateRequest
import com.ssafy.hm.data.model.AttractionLineMember
import com.ssafy.hm.data.repository.LineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LineState(
    val lineMembers: Map<Int, List<AttractionLineMember>> = emptyMap(),
    val toast: String? = null,
    val error: String? = null
)

class LineViewModel(
    private val lineRepo: LineRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LineState())
    val state: StateFlow<LineState> = _state

    fun createLine(attId: Int, userIds: List<String>) {
        viewModelScope.launch {
            try {
                val req = AttractionLineCreateRequest(attId = attId, userIds = userIds)
                val lineId = lineRepo.createLine(attId, req)
                val members = lineRepo.getLineMembers(lineId)
                val map = _state.value.lineMembers.toMutableMap()
                map[lineId] = members
                _state.value = _state.value.copy(lineMembers = map, toast = "줄서기 완료")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearToast() {
        _state.value = _state.value.copy(toast = null)
    }
}
