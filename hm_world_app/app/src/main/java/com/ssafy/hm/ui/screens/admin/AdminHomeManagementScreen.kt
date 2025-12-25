package com.ssafy.hm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeManagementScreen(
    onBack: () -> Unit,
    onOpenHomeCarouselList: () -> Unit,
    onOpenNoticeList: () -> Unit,
    onOpenBuyCarouselList: () -> Unit,
    onOpenAttractionList: () -> Unit,
    onOpenItemList: () -> Unit
) {
    val homeColor = Color(0xFF4FC3F7)
    val productColor = Color(0xFF6A5AE0)
    val rideColor = Color(0xFF68A225)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF5F2FF), Color(0xFFEDE7FF))))
    ) {
        TopAppBar(
            title = { Text("홈페이지 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminListCard("홈 화면 캐러셀 관리", "홈 캐러셀 이미지 추가/삭제", homeColor, onOpenHomeCarouselList)
            AdminListCard("공지사항 관리", "공지 등록/수정/삭제", homeColor, onOpenNoticeList)
            AdminListCard("상품 화면 캐러셀 관리", "상품 캐러셀 이미지 관리", productColor, onOpenBuyCarouselList)
            AdminListCard("상품 관리", "상품 등록/수정/삭제", productColor, onOpenItemList)
            AdminListCard("놀이기구 관리", "어트랙션 정보 관리", rideColor, onOpenAttractionList)
        }
    }
}

@Composable
private fun AdminListCard(
    title: String,
    desc: String,
    color: Color,
    onClick: () -> Unit
) {
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
                Text(title, fontWeight = FontWeight.Bold, color = color)
                Text(desc, color = Color.DarkGray.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = color)
        }
    }
}
