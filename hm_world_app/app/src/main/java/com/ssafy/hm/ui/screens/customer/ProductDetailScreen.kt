package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import java.util.Locale

@Composable
fun ProductDetailScreen(
    item: Item?,
    reviews: List<ItemReview>,
    onBack: () -> Unit,
    onAddCart: (Item) -> Unit,
    onOpenCart: () -> Unit
) {
    val background = Color(0xFFF7F3FA)
    val contentColor = Color(0xFF2A2430)
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

    Scaffold(
        containerColor = background,
        topBar = {
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
                    text = "상품 상세",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onOpenCart) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
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
        val isNew = item.itemCategory == "신상품"

        LazyColumn(
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
                        .height(240.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
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
                                    ReviewItem(review = review)
                                }
                            }
                        }
                    }
                }
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
private fun ReviewItem(review: ItemReview) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.userId ?: "익명",
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
    }
}
