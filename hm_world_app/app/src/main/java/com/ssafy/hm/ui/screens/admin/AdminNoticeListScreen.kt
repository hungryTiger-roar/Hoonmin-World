package com.ssafy.hm.ui.screens.admin

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.ui.theme.AuroraPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNoticeListScreen(
    boards: List<HomeBoard>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    val didRefresh = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!didRefresh.value) {
            onRefresh()
            didRefresh.value = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("공지사항 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = "add")
                Spacer(modifier = Modifier.width(6.dp))
                Text("게시글 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(boards, key = { it.boardId }) { board ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(board.boardTitle.orEmpty(), fontWeight = FontWeight.Bold)
                            Text(board.boardContent.orEmpty())
                            Text(board.boardDate.orEmpty())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                SmallActionButton(label = "수정") { onEdit(board.boardId) }
                                Spacer(modifier = Modifier.width(8.dp))
                                SmallActionButton(label = "삭제") { onDelete(board.boardId) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = Color.White),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label)
    }
}
