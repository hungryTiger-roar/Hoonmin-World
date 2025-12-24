package com.ssafy.hm.ui.screens.customer

import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.recommend.MiniLmRecommender
import com.ssafy.hm.recommend.RecommendationResult
import com.ssafy.hm.ui.theme.AuroraPink
import com.ssafy.hm.ui.theme.AuroraPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationDialog(
    recommender: MiniLmRecommender,
    attractions: List<Attraction>,
    items: List<Item>,
    onDismiss: () -> Unit
) {
    val attractionOptions = listOf("스릴", "가족", "어린이", "공연", "힐링")
    val goodsOptions = listOf("문구/잡화", "액세서리", "의류", "캐릭터", "기념품")
    var attractionPref by remember { mutableStateOf(attractionOptions.first()) }
    var goodsPref by remember { mutableStateOf(goodsOptions.first()) }
    var showAttractionMenu by remember { mutableStateOf(false) }
    var showGoodsMenu by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<RecommendationResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        val cardShape = RoundedCornerShape(20.dp)
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEDE3FF),
                            Color(0xFFE7D9FF),
                            Color(0xFFE0D0FF)
                        )
                    ),
                    shape = cardShape
                ),
            shape = cardShape
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("맞춤 추천", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2A2430))
                Text("취향을 선택하면 어트랙션과 굿즈를 추천해드려요!", color = Color(0xFF4B4260), fontSize = 12.sp, fontWeight = FontWeight.Bold)

                if (result == null) {
                    ExposedDropdownMenuBox(
                        expanded = showAttractionMenu,
                        onExpandedChange = { showAttractionMenu = !showAttractionMenu }
                    ) {
                        OutlinedTextField(
                            value = attractionPref,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("어트랙션 취향", color = Color.Black, fontWeight = FontWeight.Bold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAttractionMenu) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Black
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showAttractionMenu,
                            onDismissRequest = { showAttractionMenu = false }
                        ) {
                            attractionOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        attractionPref = option
                                        showAttractionMenu = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = showGoodsMenu,
                        onExpandedChange = { showGoodsMenu = !showGoodsMenu }
                    ) {
                        OutlinedTextField(
                            value = goodsPref,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("굿즈 취향", color = Color.Black, fontWeight = FontWeight.Bold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGoodsMenu) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Black
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showGoodsMenu,
                            onDismissRequest = { showGoodsMenu = false }
                        ) {
                            goodsOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        goodsPref = option
                                        showGoodsMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (result == null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    loading = true
                                    result = null
                                    val preferenceText = "어트랙션 $attractionPref, 굿즈 $goodsPref"
                                    result = withContext(Dispatchers.Default) {
                                        recommender.recommend(
                                            preferenceText = preferenceText,
                                            attractions = attractions,
                                            items = items
                                        )
                                    }
                                    loading = false
                                }
                            },
                            enabled = !loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraPink,   // 버튼 배경색
                                contentColor = Color.White,           // 글자색
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.DarkGray
                            )
                        ) {
                            Text("추천 받기")
                        }
                        Button(onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraPink,   // 버튼 배경색
                                contentColor = Color.White,           // 글자색
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.DarkGray
                            )
                        ) { Text("닫기") }
                    }
                } else {
                    Button(onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraPink,   // 버튼 배경색
                            contentColor = Color.White,           // 글자색
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.DarkGray
                        )
                    ) { Text("확인") }
                }

                if (loading) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }

                result?.let { rec ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider()
                    Text("어트랙션 추천", fontWeight = FontWeight.SemiBold, color = Color(0xFF2A2430))
                    rec.attractions.forEach { att ->
                        Text("- ${att.attName ?: "놀이기구"}", fontSize = 13.sp, color = Color(0xFF4B4260), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("굿즈 추천", fontWeight = FontWeight.SemiBold, color = Color(0xFF2A2430))
                    rec.items.forEach { item ->
                        Text("- ${item.itemName}", fontSize = 13.sp, color = Color(0xFF4B4260), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
