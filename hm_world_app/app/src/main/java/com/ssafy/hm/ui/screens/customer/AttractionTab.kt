package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ssafy.hm.data.model.AttractionReview

@Composable
fun AttractionTab(list: List<Attraction>, onSelect: (Int) -> Unit, paddingValues: PaddingValues) {
    var query by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("어트랙션 검색") }
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list.filter { it.attName?.contains(query, true) == true }) { att ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clickable { onSelect(att.attId) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = att.attPic?.takeIf { it.isNotBlank() },
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.noimage),
                            error = painterResource(id = R.drawable.noimage),
                            fallback = painterResource(id = R.drawable.noimage)
                        )
                        Column {
                            Text(att.attName ?: "", fontWeight = FontWeight.Bold)
                            Text(att.attComment ?: "", maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("최대 ${att.attCapacity}명 / 누적 ${att.attTotal}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionDetailDialog(
    att: Attraction,
    reviews: List<AttractionReview>,
    onReserve: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onReserve) { Text("대기/줄서기") }
                TextButton(onClick = onDismiss) { Text("닫기") }
            }
        },
        title = { Text(att.attName ?: "") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(att.attComment ?: "")
                Text("최대 수용: ${att.attCapacity}명 누적: ${att.attTotal}")
                Text("리뷰 (${reviews.size})", fontWeight = FontWeight.Bold)
                reviews.forEach { r -> Text("- ${r.attReviewComment ?: ""} (${r.attRating}점)") }
            }
        }
    )
}
