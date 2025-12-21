package com.ssafy.hm.ui.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NfcTagBus {
    private val _tags = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val tags = _tags.asSharedFlow()

    fun emit(tagValue: String) {
        _tags.tryEmit(tagValue)
    }
}
