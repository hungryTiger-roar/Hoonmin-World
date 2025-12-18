package com.ssafy.hm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ssafy.hm.ui.theme.HmWorldTheme
import com.ssafy.hm.ui.HmWorldApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HmWorldTheme {
                HmWorldApp()
            }
        }
    }
}
