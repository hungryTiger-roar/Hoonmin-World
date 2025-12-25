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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.ItemReview
import java.text.NumberFormat
import com.ssafy.hm.ui.util.isNewItem
import java.util.Locale

@Composable
fun ProductDetailScreen(
    item: Item?,
    reviews: List<ItemReview>,
    onBack: () -> Unit,
    onAddCart: (Item) -> Unit,
    onOpenCart: () -> Unit,
    cartCount: Int,
    userId: String?,
    userNames: Map<String, String>,
    onUpdateReview: (Int, Int, Float, String) -> Unit,
    onDeleteReview: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val background = Color(0xFFF7F3FA)
    val contentColor = Color(0xFF2A2430)
    val actionColor = Color.Black
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

    val editTarget = remember { mutableStateOf<ItemReview?>(null) }
    val deleteTarget = remember { mutableStateOf<ItemReview?>(null) }

    Scaffold(
        containerColor = background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "상품 상세",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                CartIconWithBadge(
                    cartCount = cartCount,
                    onClick = onOpenCart,
                    tint = actionColor
                )
            }
        },
        bottomBar = {
            if (item != null) {
                Button(
                    onClick = { onAddCart(item) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF9C27FF), Color(0xFFFF5AA4))
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "장바구니에 담기",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "상품 정보를 불러오는 중...", color = Color.Gray)
            }
            return@Scaffold
        }

        val priceText = NumberFormat.getNumberInstance(Locale.KOREA).format(item.itemPrice)
        val isNew = isNewItem(item.itemTime)

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                AsyncImage(
                    model = item.itemPic?.takeIf { it.isNotBlank() },
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.noimage),
                    error = painterResource(id = R.drawable.noimage),
                    fallback = painterResource(id = R.drawable.noimage)
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = cardModifier
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isNew) {
                            Surface(
                                color = Color(0xFFE91E63),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "NEW",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = item.itemName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        Text(
                            text = "${priceText}원",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE44A9B)
                        )
                        Text(
                            text = item.itemCategory ?: "",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = cardModifier
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.itemComment ?: "",
                            color = contentColor,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = cardModifier
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "수령 안내",
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        GuideRow(
                            icon = Icons.Default.LocationOn,
                            tint = Color(0xFFF06292),
                            text = "파크 내 지정된 수령 장소에서 픽업 가능합니다"
                        )
                        GuideRow(
                            icon = Icons.Default.AccessTime,
                            tint = Color(0xFFFFA726),
                            text = "주문 후 약 30분 소요됩니다"
                        )
                        GuideRow(
                            icon = Icons.Default.ReceiptLong,
                            tint = Color(0xFF42A5F5),
                            text = "모바일 결제 후 주문번호로 수령하세요"
                        )
                    }
                }
            }

            item {
                val avg = if (reviews.isNotEmpty()) {
                    reviews.map { it.itemRating.toDouble() }.average()
                } else {
                    0.0
                }
                val avgText = String.format(Locale.KOREA, "%.1f", avg)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .then(cardModifier)
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "상품 리뷰",
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Average Rating",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = avgText,
                                fontWeight = FontWeight.SemiBold,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "(${reviews.size})",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        if (reviews.isEmpty()) {
                            Text(text = "리뷰가 아직 없습니다.", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                reviews.forEach { review ->
                                    ReviewItem(
                                        review = review,
                                        displayName = userNames[review.userId] ?: review.userId ?: "익명",
                                        isMine = review.userId == userId,
                                        onEdit = { editTarget.value = review },
                                        onDelete = { deleteTarget.value = review }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editTarget.value?.let { review ->
        ReviewEditDialog(
            itemName = item?.itemName ?: "",
            initialRating = review.itemRating,
            initialComment = review.itemReviewComment ?: "",
            onDismiss = { editTarget.value = null },
            onSubmit = { rating, comment ->
                onUpdateReview(review.itemReviewId, review.itemId, rating, comment)
                android.widget.Toast
                    .makeText(context, "리뷰가 수정되었습니다.", android.widget.Toast.LENGTH_SHORT)
                    .show()
                editTarget.value = null
            }
        )
    }

    deleteTarget.value?.let { review ->
        AlertDialog(
            onDismissRequest = { deleteTarget.value = null },
            title = { Text("리뷰 삭제") },
            text = { Text("리뷰를 삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteReview(review.itemReviewId, review.itemId)
                    android.widget.Toast
                        .makeText(context, "삭제되었습니다.", android.widget.Toast.LENGTH_SHORT)
                        .show()
                    deleteTarget.value = null
                }) { Text("삭제") }
            },
            dismissButton = {
                Button(onClick = { deleteTarget.value = null }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun CartIconWithBadge(cartCount: Int, onClick: () -> Unit, tint: Color) {
    Box {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = tint)
        }
        if (cartCount > 0) {
            val label = if (cartCount > 99) "99+" else cartCount.toString()
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4D6D)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GuideRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = text, color = Color(0xFF4A4451), fontSize = 13.sp)
    }
}

@Composable
private fun ReviewItem(
    review: ItemReview,
    displayName: String,
    isMine: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayName,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2A2430),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = review.itemTime,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = review.itemRating.toString(),
                fontSize = 13.sp,
                color = Color(0xFF2A2430)
            )
        }
        Text(
            text = review.itemReviewComment ?: "",
            color = Color(0xFF4A4451),
            fontSize = 13.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        if (isMine) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "수정",
                    color = Color(0xFF6B6572),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable(onClick = onEdit)
                )
                Text(
                    text = "삭제",
                    color = Color(0xFFE57373),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onDelete)
                )
            }
        }
    }
}

@Composable
private fun ReviewEditDialog(
    itemName: String,
    initialRating: Float,
    initialComment: String,
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    val rating = remember { mutableStateOf(initialRating.toInt().coerceIn(1, 5)) }
    val comment = remember { mutableStateOf(initialComment) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
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
                androidx.compose.material3.OutlinedTextField(
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
