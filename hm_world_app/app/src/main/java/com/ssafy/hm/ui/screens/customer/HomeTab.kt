package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeTab(
    images: List<HomeImage>,
    boards: List<HomeBoard>,
    hasTicket: Boolean,
    paddingValues: PaddingValues,
    onBoardClick: (HomeBoard) -> Unit,
    onTicketPurchaseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF7F7F7))
    ) {
        HomeCarousel(images)
        Spacer(modifier = Modifier.height(20.dp))
        TicketStatusCard(
            hasTicket = hasTicket,
            onClick = onTicketPurchaseClick
        )
        Spacer(modifier = Modifier.height(24.dp))
        NoticeBoard(boards, onBoardClick = onBoardClick)
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
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
                            Text("이미지를 준비중입니다.", color = Color.Gray)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
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
fun TicketStatusCard(hasTicket: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { if (!hasTicket) onClick() },
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
            Column(modifier = Modifier.weight(1f)) {
                if (hasTicket) {
                    Text(
                        text = "훈민월드에 오신 것을 환영합니다!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "행복 가득한 하루를 보내세요!",
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
                        text = "당일권 티켓 구매하기",
                        color = Color(0xFF6200EE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (!hasTicket) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Go to purchase",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
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

