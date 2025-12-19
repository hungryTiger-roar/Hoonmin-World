package com.ssafy.hm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.ui.theme.AuroraPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionListScreen(
    attractions: List<Attraction>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onToggleAble: (Int) -> Unit
) {
    val pendingDelete = remember { mutableStateOf<Attraction?>(null) }

    if (pendingDelete.value != null) {
        val target = pendingDelete.value
        AlertDialog(
            onDismissRequest = { pendingDelete.value = null },
            confirmButton = {
                Button(
                    onClick = {
                        target?.let { onDelete(it.attId) }
                        pendingDelete.value = null
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingDelete.value = null }) {
                    Text("취소")
                }
            },
            title = { Text("삭제 확인") },
            text = { Text("정말로 삭제하시겠습니까?") }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("놀이기구 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = "추가")
                Spacer(modifier = Modifier.width(6.dp))
                Text("놀이기구 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(attractions, key = { it.attId }) { attraction ->
                    val cardColor = if (attraction.attAble) Color.White else Color(0xFFE0E0E0)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardColor)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = attraction.attPic,
                                contentDescription = "놀이기구 이미지",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                            attraction.attName?.let { Text(it, fontWeight = FontWeight.Bold) }
                            Text("최대 정원: ${attraction.attCapacity}")
                            Text(if (attraction.attAble) "운영 중단" else "운영 중", color = Color.DarkGray)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SmallActionButton(
                                    label = if (attraction.attAble) "운영 중" else "운영 중단",
                                    containerColor = if (attraction.attAble) Color(0xFF5D8BD6) else Color(0xFFF3C247)
                                ) { onToggleAble(attraction.attId) }
                                Spacer(modifier = Modifier.width(8.dp))
                                SmallActionButton(label = "수정") { onEdit(attraction.attId) }
                                Spacer(modifier = Modifier.width(8.dp))
                                SmallActionButton(
                                    label = "삭제",
                                    containerColor = Color(0xFFD65D5D)
                                ) { pendingDelete.value = attraction }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallActionButton(
    label: String,
    containerColor: Color = AuroraPurple,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = Color.White),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label)
    }
}
