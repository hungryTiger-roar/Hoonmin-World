package com.ssafy.hm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.ui.state.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    state: MainUiState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenOrderManagement: () -> Unit,
    onOpenAttractionManagement: () -> Unit,
    onOpenHomeManagement: () -> Unit,
    onOpenNotificationManagement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF5F2FF), Color(0xFFEDE7FF))))
    ) {
        TopAppBar(
            title = { Text("관리직원 대시보드", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, contentDescription = "refresh") }
                TextButton(onClick = onLogout) { Text("로그아웃", color = Color(0xFF6A5AE0)) }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("실시간 현황", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("상품", state.data.items.size, Color(0xFF6A5AE0), modifier = Modifier.weight(1f))
                StatCard("어트랙션", state.data.attractions.size, Color(0xFFFF7F41), modifier = Modifier.weight(1f))
                StatCard("공지", state.data.boards.size, Color(0xFF4FC3F7), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("바로가기", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            AdminCard("상품관리", "구매 관리 · 수령 확인", Color(0xFF6A5AE0), onClick = onOpenOrderManagement)
            AdminCard("어트랙션 관리", "대기 · 예약 · 노쇼 관리", Color(0xFFFF7F41), onClick = onOpenAttractionManagement)
            AdminCard("홈페이지 관리", "캐러셀 · 공지 · 상품 리스트", Color(0xFF4FC3F7), onClick = onOpenHomeManagement)
            AdminCard("알림 관리", "지금 보내기 · 예약 · 반복", Color(0xFF81C784), onClick = onOpenNotificationManagement)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StatCard(title: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color.copy(alpha = 0.08f))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, color = color)
            Text("$count 건", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun AdminCard(title: String, desc: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .background(color.copy(alpha = 0.08f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
                Text(desc, color = Color.DarkGray.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = color)
        }
    }
}
