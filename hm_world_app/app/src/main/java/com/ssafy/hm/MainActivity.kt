package com.ssafy.hm

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ssafy.hm.data.local.AuthStore
import com.ssafy.hm.data.model.FcmTokenRequest
import com.ssafy.hm.data.network.NetworkModule
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.hm.ui.HmWorldApp
import com.ssafy.hm.ui.state.NfcTagBus
import com.ssafy.hm.ui.theme.HmWorldTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.nio.charset.Charset

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        setContent {
            HmWorldTheme {
                HmWorldApp()
            }
        }
        initFcm()
    }

    private fun initFcm() {
        val authStore = AuthStore(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "FCM token: $token")
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

        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Topic subscription failed", task.exception)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = nfcAdapter ?: return
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        adapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val adapter = nfcAdapter ?: return
        val action = intent.action ?: return
        if (
            action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            val tagValue = readTagUserId(intent) ?: return
            lifecycleScope.launch {
                NfcTagBus.emit(tagValue)
            }
        }
    }

    private fun readTagUserId(intent: Intent): String? {
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMsgs != null && rawMsgs.isNotEmpty()) {
            val message = rawMsgs[0] as? NdefMessage
            val record = message?.records?.firstOrNull()
            val text = record?.let { parseTextRecord(it) }
            if (!text.isNullOrBlank()) {
                return text
            }
        }
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val tagId = tag?.id ?: return null
        return tagId.joinToString("") { "%02X".format(it) }
    }

    private fun parseTextRecord(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN || !record.type.contentEquals(NdefRecord.RTD_TEXT)) {
            return null
        }
        val payload = record.payload ?: return null
        if (payload.isEmpty()) return null
        val languageCodeLength = payload[0].toInt() and 0x3F
        val textStart = 1 + languageCodeLength
        if (textStart >= payload.size) return null
        return String(payload, textStart, payload.size - textStart, Charset.forName("UTF-8")).trim()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
