package com.ssafy.hm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Attraction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionListScreen(
    attractions: List<Attraction>,
    onBack: () -> Unit,
    onOpenLine: (Int) -> Unit
) {
    val queryState = remember { mutableStateOf("") }
    val filtered by remember(attractions, queryState.value) {
        derivedStateOf {
            val q = queryState.value.trim()
            if (q.isBlank()) attractions
            else attractions.filter { it.attName?.contains(q, ignoreCase = true) == true }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("어트랙션 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            }
        )

        Column(modifier = Modifier
            .padding(16.dp)
            .navigationBarsPadding()) {
            OutlinedTextField(
                value = queryState.value,
                onValueChange = { queryState.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("어트랙션 검색") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.attId }) { attraction ->
                    val cardColor = if (attraction.attAble) Color.White else Color(0xFFE0E0E0)
                    val waitCount = attraction.attTotal
                    val capacity = attraction.attCapacity
                    val waitMinutes = estimateWaitMinutes(waitCount, capacity)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenLine(attraction.attId) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardColor)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = attraction.attPic,
                                contentDescription = "어트랙션 이미지",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                            attraction.attName?.let { Text(it, fontWeight = FontWeight.Bold) }
                            Text("대기 인원: ${waitCount}명")
                            Text("예상 대기시간: ${waitMinutes}분")
                            Text("최대 탑승 인원: ${capacity}명")
                        }
                    }
                }
            }
        }
    }
}

private fun estimateWaitMinutes(waitCount: Int, capacity: Int): Int {
    if (capacity <= 0) return 0
    val groups = (waitCount + capacity - 1) / capacity
    return groups * 10
}
