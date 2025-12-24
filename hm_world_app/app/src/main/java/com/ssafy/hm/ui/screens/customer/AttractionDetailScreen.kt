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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionReview
import java.util.Locale

@Composable
fun AttractionDetailScreen(
    attraction: Attraction?,
    reviews: List<AttractionReview>,
    account: Account?,
    reservedAttId: Int?,
    allAttractions: List<Attraction>,
    userNames: Map<String, String>,
    onBack: () -> Unit,
    onReserve: () -> Unit,
    onSubmitReview: (Float, String) -> Unit,
    onUpdateReview: (Int, Int, Float, String) -> Unit,
    onDeleteReview: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val background = Color(0xFFF7F3FA)
    val contentColor = Color(0xFF2A2430)
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

    val showReviewDialog = remember { mutableStateOf(false) }
    val editTarget = remember { mutableStateOf<AttractionReview?>(null) }
    val deleteTarget = remember { mutableStateOf<AttractionReview?>(null) }

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
                    text = "어트랙션 상세",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(40.dp))
            }
        },
        bottomBar = {
            val buttonEnabled: Boolean
            val buttonText: String

            if (reservedAttId != null) {
                val reservedAttractionName = allAttractions.find { it.attId == reservedAttId }?.attName
                    ?: "알 수 없는 놀이기구"
                buttonText = "$reservedAttractionName 예약 중!"
                buttonEnabled = false
            } else {
                buttonText = "예약하기"
                buttonEnabled = true
            }

            val buttonColors = if (buttonEnabled) {
                listOf(Color(0xFF9C27FF), Color(0xFFFF5AA4))
            } else {
                listOf(Color.Gray, Color.LightGray)
            }

            Button(
                onClick = onReserve,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = buttonEnabled
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(buttonColors),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        if (attraction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "어트랙션 정보를 불러오는 중...", color = Color.Gray)
            }
            return@Scaffold
        }

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                AsyncImage(
                    model = attraction.attPic,
                    contentDescription = attraction.attName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
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
                        Text(
                            text = attraction.attName ?: "어트랙션",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        Text(
                            text = attraction.attComment ?: "",
                            fontSize = 14.sp,
                            color = Color(0xFF7A7282)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            CategoryChip(text = attraction.attCategory ?: "카테고리")
                        }
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("현재 상태", fontWeight = FontWeight.Bold, color = contentColor)
                        StatusRow(Icons.Default.LocationOn, "대기 인원", "45명", Color(0xFFE91E63))
                        StatusRow(Icons.Default.AccessTime, "예상 대기시간", "15분", Color(0xFFFFB74D))
                        StatusRow(Icons.Default.Groups, "최대 탑승 인원", "${attraction.attCapacity}명", Color(0xFF64B5F6))
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("이용 안내", fontWeight = FontWeight.Bold, color = contentColor)
                        InfoRow("예약 후 지정된 시간에 탑승 게이트로 방문해주세요")
                        InfoRow("친구와 함께 예약하면 같은 시간대에 탑승할 수 있어요")
                        InfoRow("안전을 위해 신장/건강 제한이 있을 수 있습니다")
                        InfoRow("예약 확인은 마이페이지에서 확인 가능합니다")
                    }
                }
            }

            item {
                val avg = if (reviews.isNotEmpty()) {
                    reviews.map { it.attRating.toDouble() }.average()
                } else {
                    0.0
                }
                val avgText = String.format(Locale.KOREA, "%.1f", avg)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = cardModifier
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("이용 후기", fontWeight = FontWeight.Bold, color = contentColor, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(avgText, fontWeight = FontWeight.SemiBold, color = contentColor)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("(${reviews.size})", color = Color.Gray, fontSize = 12.sp)
                        }

                        val hasMyReview = !(account?.userId).isNullOrBlank() && reviews.any { it.userId == account?.userId }
                        if (!hasMyReview) {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ){
                                Button(
                                    onClick = { showReviewDialog.value = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFEDE7F6
                                        )
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("리뷰 작성하기", color = Color(0xFF7A7282), fontSize = 12.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "이용 후기는 하루에 한 번만 작성하실 수 있습니다.",
                                color = Color(0xFF7A7282),
                                fontSize = 12.sp
                            )
                        }

                        if (reviews.isEmpty()) {
                            Text("아직 후기가 없습니다.", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                reviews.forEach { review ->
                                    ReviewRow(
                                        name = userNames[review.userId] ?: review.userId ?: "익명",
                                        rating = review.attRating,
                                        comment = review.attReviewComment ?: "",
                                        date = review.attTime,
                                        isMine = review.userId == account?.userId,
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

    if (showReviewDialog.value) {
        ReviewDialog(
            onDismiss = { showReviewDialog.value = false },
            onSubmit = { rating, comment ->
                onSubmitReview(rating, comment)
                showReviewDialog.value = false
            }
        )
    }

    editTarget.value?.let { review ->
        ReviewEditDialog(
            title = "이용 후기 수정",
            initialRating = review.attRating,
            initialComment = review.attReviewComment ?: "",
            onDismiss = { editTarget.value = null },
            onSubmit = { rating, comment ->
                onUpdateReview(review.attReviewId, review.attId, rating, comment)
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
                    onDeleteReview(review.attReviewId, review.attId)
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
private fun CategoryChip(text: String) {
    Surface(
        color = Color(0xFFF1E7FF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF7A7282),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatusRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color(0xFF4A4451), fontSize = 13.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun InfoRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFC107))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color(0xFF4A4451), fontSize = 13.sp)
    }
}

@Composable
private fun ReviewRow(
    name: String,
    rating: Float,
    comment: String,
    date: String,
    isMine: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontWeight = FontWeight.SemiBold, color = Color(0xFF2A2430), modifier = Modifier.weight(1f))
            Text(date, color = Color.Gray, fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(String.format(Locale.KOREA, "%.1f", rating), fontSize = 12.sp, color = Color(0xFF2A2430))
        }
        Text(comment, color = Color(0xFF4A4451), fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
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
private fun ReviewDialog(onDismiss: () -> Unit, onSubmit: (Float, String) -> Unit) {
    ReviewEditDialog(
        title = "이용 후기 작성",
        initialRating = 5f,
        initialComment = "",
        onDismiss = onDismiss,
        onSubmit = onSubmit
    )
}

@Composable
private fun ReviewEditDialog(
    title: String,
    initialRating: Float,
    initialComment: String,
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    val rating = remember { mutableStateOf(initialRating.toInt().coerceIn(1, 5)) }
    val comment = remember { mutableStateOf(initialComment) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
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
                    Text(rating.value.toString(), fontWeight = FontWeight.SemiBold)
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
