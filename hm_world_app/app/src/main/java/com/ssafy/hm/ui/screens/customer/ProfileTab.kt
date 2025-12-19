package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders

import androidx.compose.foundation.layout.PaddingValues

@Composable
fun ProfileTab(
    orders: List<Orders>,
    details: Map<Int, List<OrderDetail>>,
    onReceive: (Int) -> Unit,
    friends: List<Friend>,
    availableFriends: List<Friend>,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (Int) -> Unit,
    onLogout: () -> Unit,
    paddingValues: PaddingValues
) {
    var friendSearch by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("내 정보", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("주문 이력", fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 240.dp)) {
            if (orders.isEmpty()) {
                item { Text("주문 이력이 없습니다.") }
            } else {
                items(orders) { order ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("주문번호 ${order.orderId} / 매장 ${order.orderStore}")
                            Text("상태: ${if (order.orderReceived) "수령완료" else "준비중"}")
                            details[order.orderId]?.forEach { d -> Text("- item ${d.itemId} x${d.orderQuantity}") }
                            if (!order.orderReceived) {
                                TextButton(onClick = { onReceive(order.orderId) }) { Text("수령 처리") }
                            }
                        }
                    }
                }
            }
        }

        Text("친구 관리", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = friendSearch,
            onValueChange = { friendSearch = it },
            label = { Text("친구 아이디 검색/추가") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAddFriend(friendSearch) }) { Text("추가") }
            Button(onClick = { friendSearch = "" }) { Text("초기화") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.heightIn(max = 140.dp)) {
            items(friends.filter { it.friendId?.contains(friendSearch, true) == true || friendSearch.isBlank() }) { fr ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fr.friendId)
                    TextButton(onClick = { onRemoveFriend(fr.id) }) { Text("삭제") }
                }
            }
        }
        Text("줄서기 진행(현재 티켓/줄 없음)", fontSize = 12.sp, color = Color.Gray)
        FlowFriends(availableFriends)

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
            Text("로그아웃")
        }
    }
}

@Composable
private fun FlowFriends(friends: List<Friend>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        friends.forEach {
            Text(
                text = it.friendId,
                modifier = Modifier
                    .background(Color(0xFFE8E8F5), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
