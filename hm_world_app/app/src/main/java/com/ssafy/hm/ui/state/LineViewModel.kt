package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.AttractionLineCreateRequest
import com.ssafy.hm.data.model.AttractionLineMember
import com.ssafy.hm.data.repository.LineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
            runCatching {
                val req = AttractionLineCreateRequest(attId = attId, userIds = userIds)
                val lineId = lineRepo.createLine(attId, req)
                val members = lineRepo.getLineMembers(lineId)
                Pair(lineId, members)
            }.onSuccess { (lineId, members) ->
                _state.update {
                    val map = it.lineMembers.toMutableMap()
                    map[lineId] = members
                    it.copy(lineMembers = map, toast = "ì¤„ì„œê¸??„ë£Œ")
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
