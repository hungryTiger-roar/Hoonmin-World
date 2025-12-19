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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.HomeImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeCarouselListScreen(
    images: List<HomeImage>,
    onBack: () -> Unit,
    onAddImage: () -> Unit,
    onDeleteImage: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("홈 캐러셀 관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onAddImage, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = "추가")
                Spacer(modifier = Modifier.width(6.dp))
                Text("이미지 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(images, key = { it.homeId }) { image ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = image.homeImage,
                                contentDescription = "홈 이미지",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(image.homeImage, modifier = Modifier.weight(1f))
                                OutlinedButton(onClick = { onDeleteImage(image.homeId) }) {
                                    Text("삭제")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
