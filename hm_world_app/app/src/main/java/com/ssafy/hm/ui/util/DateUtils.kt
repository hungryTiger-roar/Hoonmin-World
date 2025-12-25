package com.ssafy.hm.ui.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal fun isNewItem(releaseDate: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val release = LocalDateTime.parse(releaseDate, formatter)
        val now = LocalDateTime.now()
        ChronoUnit.DAYS.between(release, now) in 0..30
    } catch (e: Exception) {
        false
    }
}
