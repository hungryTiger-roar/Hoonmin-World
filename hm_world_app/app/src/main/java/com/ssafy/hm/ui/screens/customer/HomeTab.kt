package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.ui.state.LineState
import com.ssafy.hm.ui.state.OrderState
import kotlinx.coroutines.delay

@Composable
fun HomeTab(
    images: List<HomeImage>,
    boards: List<HomeBoard>,
    orderState: OrderState,
    lineState: LineState,
    onDeleteBoard: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { maxOf(images.size, 1) })

    val hasTicket = orderState.orders.isNotEmpty()
    val firstLine = lineState.lineMembers.values.firstOrNull()
    val waitingCount = firstLine?.size ?: 0
    val expectedMinutes = if (waitingCount == 0) 0 else ((waitingCount - 1) / 20 + 1) * 10

    LaunchedEffect(pagerState.pageCount) {
        while (pagerState.pageCount > 1) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8E6FF), Color.White)
                )
            )
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("홈 갤러리", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5A4FD3))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            HorizontalPager(state = pagerState) { page ->
                val image = images.getOrNull(page)
                if (image != null) {
                    AsyncImage(
                        model = image.homeImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEFEFFF)),
                        contentAlignment = Alignment.Center
                    ) { Text("이미지를 준비 중입니다") }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A5AE0).copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("훈민월드에서 신나는 하루!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A3FB6))
                if (hasTicket) {
                    Text("즐거운 이용 되세요!", fontWeight = FontWeight.Bold, color = Color(0xFF6A5AE0))
                } else {
                    Text("아직 구매한 티켓이 없습니다. 티켓을 구매해보세요.", color = Color.DarkGray)
                    Button(
                        onClick = { /* TODO: 티켓 구매 화면으로 이동 */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("티켓 구매하기") }
                }
                if (waitingCount > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("대기 중", fontWeight = FontWeight.Bold)
                            Text("예상 대기시간 ${expectedMinutes}분", color = Color(0xFF6A5AE0))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { /* TODO: 개인 대기 취소 */ }) { Text("대기 취소") }
                                Button(onClick = { /* TODO: 전체 대기 취소 */ }) { Text("전체 취소") }
                            }
                        }
                    }
                }
            }
        }

        Text("공지/게시판", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        boards.forEach { board ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(board.boardTitle ?: "", fontWeight = FontWeight.Bold)
                    }
                    Text(board.boardContent ?: "", maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(board.boardDate ?: "", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
