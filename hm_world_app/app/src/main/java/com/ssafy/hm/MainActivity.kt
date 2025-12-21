package com.ssafy.hm

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ssafy.hm.ui.theme.HmWorldTheme
import com.ssafy.hm.ui.HmWorldApp
import com.ssafy.hm.ui.state.NfcTagBus
import kotlinx.coroutines.launch
import java.nio.charset.Charset

class MainActivity : ComponentActivity() {
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
}
