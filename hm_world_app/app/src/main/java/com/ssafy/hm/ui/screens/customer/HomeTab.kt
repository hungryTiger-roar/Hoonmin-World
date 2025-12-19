package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.ui.state.OrderState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeTab(
    images: List<HomeImage>,
    boards: List<HomeBoard>,
    orderState: OrderState,
    paddingValues: PaddingValues,
    onBoardClick: (HomeBoard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF7F7F7))
    ) {
        TopHeader()
        Spacer(modifier = Modifier.height(16.dp))
        HomeCarousel(images)
        Spacer(modifier = Modifier.height(24.dp))
        TicketStatusCard(hasTicket = orderState.orders.isNotEmpty())
        Spacer(modifier = Modifier.height(24.dp))
        NoticeBoard(boards, onBoardClick = onBoardClick)
    }
}

@Composable
fun TopHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF8A2BE2), Color(0xFFFF69B4))
                )
            )
            .padding(top = 24.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Text(
                text = "🌙 훈민월드",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "마법같은 하루를 만들어드릴게요 ✨",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun HomeCarousel(images: List<HomeImage>) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

    LaunchedEffect(pagerState.pageCount) {
        while (pagerState.pageCount > 1) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box {
                HorizontalPager(state = pagerState) { page ->
                    val image = images.getOrNull(page)
                    if (image != null) {
                        AsyncImage(
                            model = image.homeImage,
                            contentDescription = "Home Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("사진을 등록해주세요", color = Color.Gray)
                        }
                    }
                }
                // Text overlay on the image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 300f
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "신규 놀이기구 오픈!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(pagerState.pageCount) { index ->
                val color = if (pagerState.currentPage == index) Color(0xFF6200EE) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}


@Composable
fun TicketStatusCard(hasTicket: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable {
                // TODO: 티켓 구매 페이지로 네비게이션하는 로직을 여기에 추가하세요.
                // 예: navController.navigate("ticketPurchase")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = "Ticket Icon",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF6200EE)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                if (hasTicket) {
                    Text(
                        text = "구매한 티켓이 있습니다.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "즐거운 시간 보내세요!",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "현재 구매한 티켓이 없습니다.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "탭하여 티켓 구매하기 →",
                        color = Color(0xFF6200EE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun NoticeBoard(boards: List<HomeBoard>, onBoardClick: (HomeBoard) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notice Icon",
                tint = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "공지사항",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val twoWeeksAgo = Calendar.getInstance().apply {
                    add(Calendar.WEEK_OF_YEAR, -2)
                }

                boards.forEach { board ->
                    var formattedDate = ""
                    var isNew = false

                    board.boardDate?.let { dateString ->
                        try {
                            val boardDate = inputFormat.parse(dateString)
                            if (boardDate != null) {
                                formattedDate = outputFormat.format(boardDate)
                                val boardCalendar = Calendar.getInstance().apply { time = boardDate }
                                isNew = !boardCalendar.before(twoWeeksAgo)
                            }
                        } catch (e: Exception) {
                            // Handle parsing exception if the format is unexpected
                            formattedDate = "날짜 없음"
                            isNew = false
                        }
                    }

                    NoticeItem(
                        title = board.boardTitle,
                        date = formattedDate,
                        isNew = isNew,
                        onClick = { onBoardClick(board) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun NoticeItem(title: String?, date: String, isNew: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isNew) {
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "NEW",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = title ?: "",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to notice",
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date,
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
