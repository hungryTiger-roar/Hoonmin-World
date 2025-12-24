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
    val reservedAttId: Int? = null,
    val reservedLineId: Int? = null,
    val reservedAheadCount: Int? = null,
    val toast: String? = null,
    val error: String? = null
)

class LineViewModel(
    private val lineRepo: LineRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LineState())
    val state: StateFlow<LineState> = _state

    fun createLine(attId: Int, userIds: List<String>, onResult: (Boolean) -> Unit) {
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
                    it.copy(
                        lineMembers = map,
                        reservedAttId = attId,
                        reservedLineId = lineId,
                        reservedAheadCount = null
                    )
                }
                onResult(true)
                refreshReservationStatus()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
                onResult(false)
            }
        }
    }

    fun findReservation(userId: String, attIds: List<Int>) {
        if (attIds.isEmpty()) {
            return
        }
        viewModelScope.launch {
            runCatching {
                for (attId in attIds) {
                    val lines = lineRepo.getLinesByAttraction(attId)
                    val line = lines.firstOrNull { it.members.any { member -> member.userId == userId } }
                    if (line != null) {
                        val aheadCount = lines.filter { it.lineId < line.lineId }.sumOf { it.members.size }
                        return@runCatching Triple(attId, line.lineId, aheadCount)
                    }
                }
                null
            }.onSuccess { result ->
                if (result == null) {
                    _state.update {
                        it.copy(reservedAttId = null, reservedLineId = null, reservedAheadCount = null, error = null)
                    }
                } else {
                    _state.update {
                        it.copy(
                            reservedAttId = result.first,
                            reservedLineId = result.second,
                            reservedAheadCount = result.third,
                            error = null
                        )
                    }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun refreshReservationStatus() {
        val attId = _state.value.reservedAttId ?: return
        val lineId = _state.value.reservedLineId ?: return
        viewModelScope.launch {
            runCatching {
                val lines = lineRepo.getLinesByAttraction(attId)
                lines.filter { it.lineId < lineId }.sumOf { it.members.size }
            }.onSuccess { aheadCount ->
                _state.update { it.copy(reservedAheadCount = aheadCount, error = null) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
