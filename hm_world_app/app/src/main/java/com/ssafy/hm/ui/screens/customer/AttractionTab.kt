package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionReview
import com.ssafy.hm.data.model.FriendWithDetails

@Composable
fun AttractionTab(list: List<Attraction>, onSelect: (Int) -> Unit, paddingValues: PaddingValues) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF9F9F9))
    ) {
        AttractionTopBar()

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AttractionSearchBar(query = query, onQueryChange = { query = it })
            Spacer(modifier = Modifier.height(16.dp))
            AttractionCategoryButtons(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val filteredList = list.filter {
            (selectedCategory == "전체" || it.attCategory == selectedCategory) &&
                    (query.isBlank() || it.attName?.contains(query, ignoreCase = true) == true)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList, key = { it.attId }) { attraction ->
                AttractionInfoCard(attraction = attraction, onCardClick = { onSelect(attraction.attId) })
            }
        }
    }
}

@Composable
fun AttractionTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "어트랙션 예약", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AttractionSearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("어트랙션 검색") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun AttractionCategoryButtons(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("전체", "인기", "짧은 대기시간", "스릴", "가족", "어린이")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Button(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF6200EE) else Color.White,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(category)
            }
        }
    }
}

@Composable
fun AttractionInfoCard(attraction: Attraction, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = attraction.attName ?: "이름 없음",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = "Capacity", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("최대 ${attraction.attCapacity}명", fontSize = 14.sp, color = Color.Gray)
                }
                Text(
                    text = attraction.attComment ?: "",
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // 예약 명수 (임시 주석)
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.LocationOn, contentDescription = "Waiting Count", modifier = Modifier.size(16.dp), tint = Color.Red.copy(alpha=0.7f))
                     Spacer(modifier = Modifier.width(4.dp))
                     Text("대기 120명", fontSize = 14.sp, color = Color.Gray) // 예시: "대기 ${attraction.waitingCount}명"
                 }
                // 예상 대기 시간 (임시 주석)
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.AccessTime, contentDescription = "Wait Time", modifier = Modifier.size(16.dp), tint = Color.Gray)
                     Spacer(modifier = Modifier.width(4.dp))
                     Text("예상 50분", fontSize = 14.sp, color = Color.Gray) // 예시: "예상 ${attraction.waitTime}분"
                 }
            }
            Spacer(modifier = Modifier.width(16.dp))
            AsyncImage(
                model = attraction.attPic?.takeIf { it.isNotBlank() },
                contentDescription = attraction.attName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.noimage),
                error = painterResource(id = R.drawable.noimage),
                fallback = painterResource(id = R.drawable.noimage)
            )
        }
    }
}


@Composable
fun AttractionDetailDialog(
    att: Attraction,
    reviews: List<AttractionReview>,
    availableFriends: List<FriendWithDetails>,
    initialSelectedIds: Set<String>,
    onReserve: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIds by remember(att, initialSelectedIds) {
        mutableStateOf(initialSelectedIds)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = { onReserve(selectedIds.toList()) }) { Text("??/???") }
                TextButton(onClick = onDismiss) { Text("??") }
            }
        },
        title = { Text(att.attName ?: "") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(att.attComment ?: "")
                Text("??: ${att.attCapacity}? / ??: ${att.attTotal}")
                Text("?? (${reviews.size})", fontWeight = FontWeight.Bold)
                reviews.forEach { r -> Text("- ${r.attReviewComment ?: ""} (${r.attRating}?)") }
                if (availableFriends.isNotEmpty()) {
                    Text("?? ??? ???? ?????.", fontSize = 12.sp, color = Color.Gray)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableFriends.forEach { friend ->
                            val friendId = friend.friend.friendId
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${friend.account.name ?: "????"} ($friendId)")
                                Checkbox(
                                    checked = selectedIds.contains(friendId),
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) {
                                            selectedIds + friendId
                                        } else {
                                            selectedIds - friendId
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

