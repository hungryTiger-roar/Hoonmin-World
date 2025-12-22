package com.ssafy.hm.data.model

data class FcmTokenRequest(
    val userId: String,
    val token: String
)

data class NotificationSendRequest(
    val title: String,
    val body: String,
    val topic: String? = null,
    val targetUserIds: List<String>? = null
)

data class NotificationScheduleRequest(
    val title: String,
    val body: String,
    val scheduledAt: String
)

data class NotificationRepeatRequest(
    val title: String,
    val body: String,
    val repeatDays: String,
    val repeatTime: String
)

data class NotificationUpdateRequest(
    val title: String,
    val body: String,
    val scheduledAt: String? = null,
    val repeatDays: String? = null,
    val repeatTime: String? = null
)

data class PushNotification(
    val id: Int,
    val title: String,
    val body: String,
    val type: String,
    val scheduledAt: String? = null,
    val repeatDays: String? = null,
    val repeatTime: String? = null,
    val status: String? = null,
    val lastSentAt: String? = null
)
