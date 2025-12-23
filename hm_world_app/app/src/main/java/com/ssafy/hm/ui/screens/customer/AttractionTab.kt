package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Attraction

@Composable
fun AttractionTab(
    list: List<Attraction>,
    onSelect: (Int) -> Unit,
    paddingValues: PaddingValues
) {
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
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList, key = { it.attId }) { attraction ->
                AttractionInfoCard(attraction = attraction, onCardClick = { onSelect(attraction.attId) })
            }
        }
    }
}

@Composable
private fun AttractionTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "어트랙션 예약", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AttractionSearchBar(query: String, onQueryChange: (String) -> Unit) {
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
private fun AttractionCategoryButtons(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("전체", "스릴", "가족", "어린이", "공연")
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
private fun AttractionInfoCard(attraction: Attraction, onCardClick: () -> Unit) {
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
                    text = attraction.attName ?: "어트랙션",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = "Capacity",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("최대 ${attraction.attCapacity}명", fontSize = 14.sp, color = Color.Gray)
                }
                Text(
                    text = attraction.attComment ?: "",
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Waiting Count",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("대기 120명", fontSize = 14.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "Wait Time",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("대기 50분", fontSize = 14.sp, color = Color.Gray)
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
