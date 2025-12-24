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
    val waitingAttId: Int? = null,
    val waitingUserIds: Set<String> = emptySet(),
    val waitingCounts: Map<Int, Int> = emptyMap(),
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

    fun refreshReservationStatus(userId: String? = null) {
        val attId = _state.value.reservedAttId ?: return
        val lineId = _state.value.reservedLineId ?: return
        viewModelScope.launch {
            runCatching {
                val lines = lineRepo.getLinesByAttraction(attId)
                val currentLine = lines.firstOrNull { it.lineId == lineId } ?: return@runCatching null
                if (userId != null && currentLine.members.none { it.userId == userId }) {
                    return@runCatching null
                }
                lines.filter { it.lineId < lineId }.sumOf { it.members.size }
            }.onSuccess { aheadCount ->
                if (aheadCount == null) {
                    _state.update {
                        it.copy(
                            reservedAttId = null,
                            reservedLineId = null,
                            reservedAheadCount = null,
                            error = null
                        )
                    }
                } else {
                    _state.update { it.copy(reservedAheadCount = aheadCount, error = null) }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadWaitingCounts(attIds: List<Int>) {
        if (attIds.isEmpty()) {
            _state.update { it.copy(waitingCounts = emptyMap()) }
            return
        }
        viewModelScope.launch {
            runCatching {
                val counts = mutableMapOf<Int, Int>()
                attIds.forEach { attId ->
                    val lines = lineRepo.getLinesByAttraction(attId)
                    counts[attId] = lines.sumOf { it.members.size }
                }
                counts.toMap()
            }.onSuccess { counts ->
                _state.update { it.copy(waitingCounts = counts, error = null) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadWaitingUsers(attIds: List<Int>) {
        viewModelScope.launch {
            runCatching {
                attIds
                    .flatMap { attId -> lineRepo.getLinesByAttraction(attId) }
                    .flatMap { it.members }
                    .map { it.userId }
                    .toSet()
            }.onSuccess { users ->
                _state.update {
                    it.copy(
                        waitingAttId = attIds.firstOrNull(),
                        waitingUserIds = users,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearWaitingUsers() {
        _state.update { it.copy(waitingAttId = null, waitingUserIds = emptySet()) }
    }

    fun cancelReservation(userId: String) {
        val lineId = _state.value.reservedLineId ?: return
        viewModelScope.launch {
            runCatching { lineRepo.deleteLineMember(lineId, userId) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            reservedAttId = null,
                            reservedLineId = null,
                            reservedAheadCount = null,
                            waitingUserIds = it.waitingUserIds - userId,
                            toast = "예약이 취소되었습니다.",
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    val stillMember = runCatching {
                        lineRepo.getLineMembers(lineId).any { it.userId == userId }
                    }.getOrNull()
                    if (stillMember == false) {
                        _state.update {
                            it.copy(
                                reservedAttId = null,
                                reservedLineId = null,
                                reservedAheadCount = null,
                                waitingUserIds = it.waitingUserIds - userId,
                                toast = "예약이 취소되었습니다.",
                                error = null
                            )
                        }
                    } else {
                        _state.update { it.copy(error = e.message) }
                    }
                }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
