package com.ssafy.hm.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.model.FcmTokenRequest
import com.ssafy.hm.data.network.NetworkModule
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.hm.MainActivity
import com.ssafy.hm.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HmFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token: $token")
        val authStore = AuthStore(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val userId = authStore.userIdFlow.firstOrNull()
            if (!userId.isNullOrBlank()) {
                runCatching {
                    NetworkModule.api.registerFcmToken(FcmTokenRequest(userId, token))
                }.onFailure {
                    Log.w(TAG, "Failed to register FCM token", it)
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]
        if (title.isNullOrBlank() && body.isNullOrBlank()) {
            return
        }
        showNotification(title ?: "Hm World", body ?: "")
    }

    private fun showNotification(title: String, body: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        private const val TAG = "HmFcmService"
        private const val DEFAULT_CHANNEL_ID = "hm_default"
    }
}
