package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MapTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF1F5)),
        contentAlignment = Alignment.Center
    ) { Text("지도 API 연동 예정", color = Color.Gray) }
}
