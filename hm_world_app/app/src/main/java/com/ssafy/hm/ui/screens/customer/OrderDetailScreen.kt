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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun OrderDetailScreen(
    orders: List<Orders>,
    orderDetails: Map<Int, List<OrderDetail>>,
    items: Map<Int, Item>,
    onBack: () -> Unit,
    onGoShopping: () -> Unit,
    onRefresh: () -> Unit,
    userId: String?,
    onSubmitReview: (Int, Float, String) -> Unit,
    onReorder: (Orders, List<OrderDetail>) -> Unit
) {
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F1FA), Color(0xFFFCFAFF))
    )
    val context = LocalContext.current
    val reviewTarget = remember { mutableStateOf<Item?>(null) }
    val reviewTargetDetail = remember { mutableStateOf<OrderDetail?>(null) }
    val reviewedMap = remember { mutableStateMapOf<Int, Boolean>() }
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        LaunchedEffect(Unit) {
            while (true) {
                onRefresh()
                delay(5000)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "구매내역",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2A2430),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        if (orders.isEmpty()) {
            EmptyOrderHistory(onGoShopping = onGoShopping)
            return
        }

        val sortedOrders = orders.sortedByDescending { it.orderTime }
        LaunchedEffect(sortedOrders.size) {
            if (sortedOrders.isNotEmpty()) {
                listState.scrollToItem(0)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(sortedOrders, key = { it.orderId }) { order ->
                val details = orderDetails[order.orderId].orEmpty()
                OrderCard(
                    order = order,
                    details = details,
                    items = items,
                    reviewedMap = reviewedMap,
                    onWriteReview = { item, detail ->
                        if (userId != null) {
                            reviewTarget.value = item
                            reviewTargetDetail.value = detail
                        }
                    },
                    onReorder = { onReorder(order, details) }
                )
            }
        }
    }

    if (reviewTarget.value != null && reviewTargetDetail.value != null) {
        ItemReviewDialog(
            itemName = reviewTarget.value?.itemName ?: "",
            onDismiss = {
                reviewTarget.value = null
                reviewTargetDetail.value = null
            },
            onSubmit = { rating, comment ->
                val itemId = reviewTarget.value?.itemId
                val detailId = reviewTargetDetail.value?.detailId
                if (itemId != null) {
                    onSubmitReview(itemId, rating, comment)
                    if (detailId != null) {
                        reviewedMap[detailId] = true
                    }
                    android.widget.Toast
                        .makeText(
                            context,
                            "리뷰가 등록되었습니다.",
                            android.widget.Toast.LENGTH_SHORT
                        )
                        .show()
                }
                reviewTarget.value = null
                reviewTargetDetail.value = null
            }
        )
    }
}

@Composable
private fun EmptyOrderHistory(onGoShopping: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = Color(0xFFEAD8FF),
            shape = CircleShape,
            modifier = Modifier.size(84.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = Color(0xFFB259FF),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("구매 내역이 없어요", color = Color(0xFF6B6572), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onGoShopping,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB259FF)),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text("쇼핑하러 가기", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OrderCard(
    order: Orders,
    details: List<OrderDetail>,
    items: Map<Int, Item>,
    reviewedMap: Map<Int, Boolean>,
    onWriteReview: (Item, OrderDetail) -> Unit,
    onReorder: () -> Unit
) {
    val total = details.sumOf { detail ->
        val itemPrice = items[detail.itemId]?.itemPrice ?: 0
        itemPrice * detail.orderQuantity
    }
    val totalText = NumberFormat.getNumberInstance(Locale.KOREA).format(total)
    val statusText = if (order.orderReceived) "수령완료" else "준비중"
    val statusColor = if (order.orderReceived) Color(0xFF5DBB63) else Color(0xFFFFC857)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.orderTime, color = Color(0xFF6B6572), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "주문번호: HM${order.orderId}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2A2430),
                        fontSize = 13.sp
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                details.forEach { detail ->
                    val item = items[detail.itemId]
                    val isReviewed = detail.detailReview || reviewedMap[detail.detailId] == true
                    OrderItemRow(
                        item = item,
                        qty = detail.orderQuantity,
                        detailReview = isReviewed,
                        orderReceived = order.orderReceived,
                        onWriteReview = {
                            if (item != null && !isReviewed) {
                                onWriteReview(item, detail)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("수령 매장", color = Color(0xFF6B6572), fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(storeName(order.orderStore), color = Color(0xFF2A2430), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("총 결제 금액", color = Color(0xFF6B6572), fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${totalText}원",
                        color = Color(0xFFB259FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                if (order.orderReceived) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onReorder,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("🩵 재주문 🩷", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(
    item: Item?,
    qty: Int,
    detailReview: Boolean,
    orderReceived: Boolean,
    onWriteReview: () -> Unit
) {
    val priceText = NumberFormat.getNumberInstance(Locale.KOREA).format(item?.itemPrice ?: 0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item?.itemPic?.takeIf { it.isNotBlank() },
            contentDescription = item?.itemName,
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.noimage),
            error = painterResource(id = R.drawable.noimage),
            fallback = painterResource(id = R.drawable.noimage)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item?.itemName ?: "상품 정보 없음",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color(0xFF2A2430),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("수량: ${qty}개", fontSize = 11.sp, color = Color(0xFF7A7282))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${priceText}원",
                color = Color(0xFFB259FF),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (orderReceived) {
                Spacer(modifier = Modifier.height(6.dp))
        Button(
            onClick = onWriteReview,
            enabled = !detailReview,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (detailReview) Color(0xFFE0E0E0) else Color(0xFFB259FF)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
            Text(
                text = if (detailReview) "리뷰 작성 완료" else "리뷰 작성하기",
                color = if (detailReview) Color.Gray else Color.White,
                fontSize = 11.sp
            )
        }
            }
        }
    }
}

private fun storeName(storeId: Int): String {
    return when (storeId) {
        1 -> "래리랜드 매장"
        2 -> "중앙광장 매장"
        3 -> "메인게이트 매장"
        else -> "미정"
    }
}

@Composable
private fun ItemReviewDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    val rating = remember { mutableStateOf(5) }
    val comment = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "리뷰 작성",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = itemName,
                    color = Color(0xFF7A7282),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text("별점", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        val isActive = index < rating.value
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isActive) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { rating.value = index + 1 }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = rating.value.toString(),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2A2430)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("리뷰 내용", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = comment.value,
                    onValueChange = { comment.value = it },
                    placeholder = { Text("리뷰를 작성해주세요...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDE7F6))
                    ) {
                        Text("취소", color = Color(0xFF6B6572))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onSubmit(rating.value.toFloat(), comment.value) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB259FF))
                    ) {
                        Text("확인", color = Color.White)
                    }
                }
            }
        }
    }
}


