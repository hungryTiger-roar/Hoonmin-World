package com.ssafy.hm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.ui.theme.AuroraPurple

private data class StoreOption(val id: Int, val label: String)

private val storeOptions = listOf(
    StoreOption(1, "메인게이트 매장"),
    StoreOption(2, "중앙광장 매장"),
    StoreOption(3, "퍼레이드로 매장")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagementScreen(
    orders: List<Orders>,
    orderDetails: Map<Int, List<OrderDetail>>,
    accounts: Map<String, Account>,
    items: Map<Int, Item>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onReceive: (Int, String?) -> Unit
) {
    val fieldBackground = Color.Transparent
    val fieldBorder = Color.White
    val fieldText = Color.White
    val storeButtonText = Color(0xFF5A3E2B)

    var query by remember { mutableStateOf("") }
    var includeReceived by remember { mutableStateOf(false) }
    var selectedStore by remember { mutableStateOf<StoreOption?>(null) }
    var filterDialogOpen by remember { mutableStateOf(false) }
    var expandedOrderId by remember { mutableStateOf<Int?>(null) }

    val filteredOrders = run {
        val trimmed = query.trim().lowercase()
        orders.filter { order ->
            if (includeReceived && !order.orderReceived) return@filter false
            if (!includeReceived && order.orderReceived) return@filter false
            if (selectedStore != null && order.orderStore != selectedStore!!.id) return@filter false
            if (trimmed.isBlank()) return@filter true
            val account = order.userId?.let { accounts[it] }
            val name = account?.name?.lowercase().orEmpty()
            val phone = account?.phone?.lowercase().orEmpty()
            val userId = order.userId?.lowercase().orEmpty()
            val orderId = order.orderId.toString()
            name.contains(trimmed) ||
                phone.contains(trimmed) ||
                userId.contains(trimmed) ||
                orderId.contains(trimmed)
        }
    }

    if (filterDialogOpen) {
        StoreFilterDialog(
            selected = selectedStore,
            onDismiss = { filterDialogOpen = false },
            onSelect = {
                selectedStore = it
                filterDialogOpen = false
            },
            onClear = {
                selectedStore = null
                filterDialogOpen = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("상품관리", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "refresh")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("고객명/전화번호 검색", color = fieldText.copy(alpha = 0.75f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = fieldBorder,
                        unfocusedBorderColor = fieldBorder,
                        focusedTextColor = fieldText,
                        unfocusedTextColor = fieldText,
                        cursorColor = fieldBorder,
                        focusedContainerColor = fieldBackground,
                        unfocusedContainerColor = fieldBackground
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = includeReceived, onCheckedChange = { includeReceived = it })
                Text("수령 완료 고객 표시")
                if (loading) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("불러오는 중...", color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = { filterDialogOpen = true },
                    modifier = Modifier.widthIn(max = 160.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = storeButtonText,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, fieldBorder),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "filter",
                            tint = Color(0xFFFF9800))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        selectedStore?.label ?: "매장 필터",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (filteredOrders.isEmpty()) {
                Text("표시할 주문이 없습니다.", color = Color.Gray)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    items(filteredOrders, key = { it.orderId }) { order ->
                        val account = order.userId?.let { accounts[it] }
                        val isExpanded = expandedOrderId == order.orderId
                        AdminOrderCard(
                            order = order,
                            account = account,
                            storeLabel = storeOptions.firstOrNull { it.id == order.orderStore }?.label,
                            details = orderDetails[order.orderId].orEmpty(),
                            items = items,
                            expanded = isExpanded,
                            onToggle = {
                                expandedOrderId = if (isExpanded) null else order.orderId
                            },
                            onReceive = {
                                onReceive(order.orderId, account?.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: Orders,
    account: Account?,
    storeLabel: String?,
    details: List<OrderDetail>,
    items: Map<Int, Item>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onReceive: () -> Unit
) {
    val receivedColor = if (order.orderReceived) Color(0xFF4CAF50) else Color(0xFFFFB74D)
    val name = account?.name ?: "알수없음"
    val phone = account?.phone ?: "-"
    val storeText = storeLabel ?: "매장 ${order.orderStore}"
    val detailCount = details.sumOf { it.orderQuantity }
    val totalPrice = details.sumOf { detail ->
        val price = items[detail.itemId]?.itemPrice ?: 0
        price * detail.orderQuantity
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .clickable { onToggle() }
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$phone", color = Color.DarkGray, fontSize = 12.sp)
                }
                Text(
                    if (order.orderReceived) "수령 완료" else "수령 미완료",
                    color = receivedColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
            Text("주문번호 ${order.orderId} · $storeText", fontSize = 12.sp)
            Text("주문시간 ${order.orderTime}", fontSize = 12.sp)
            Text("상품 ${detailCount}개 · 총 ${totalPrice}원", fontSize = 12.sp)

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("상세 구매내역", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                details.forEach { detail ->
                    val item = items[detail.itemId]
                    val label = item?.itemName ?: "상품 ${detail.itemId}"
                    val price = item?.itemPrice ?: 0
                    val imageUrl = item?.itemPic
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "상품 이미지",
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(36.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("$label x${detail.orderQuantity}", fontSize = 12.sp)
                        }
                        Text("가격 ${price}원", fontSize = 12.sp)
                    }
                }
                if (!order.orderReceived) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onReceive,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = AuroraPurple,
                            contentColor = Color.White
                        )
                    ) {
                        Text("수령 완료", fontSize = 13.sp)
                    }
                } else if (!order.orderReceivedTime.isNullOrBlank()) {
                    Text("수령시간 ${order.orderReceivedTime}", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StoreFilterDialog(
    selected: StoreOption?,
    onDismiss: () -> Unit,
    onSelect: (StoreOption) -> Unit,
    onClear: () -> Unit
) {
    val fieldBackground = Color(0xFFF8F6F2).copy(alpha = 0.75f)
    val fieldBorder = Color(0xFF5A3E2B)
    val fieldText = Color(0xFF5A3E2B)

    var query by remember { mutableStateOf("") }
    val filtered = storeOptions.filter { it.label.contains(query.trim()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = fieldBorder, contentColor = Color.White)
            ) { Text("닫기") }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onClear,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = fieldText,
                    containerColor = fieldBackground
                ),
                border = BorderStroke(1.dp, fieldBorder),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) { Text("전체 매장") }
        },
        title = { Text("매장 필터", color = fieldText) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                filtered.forEach { store ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(store) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(store.label, fontSize = 16.sp, color = fieldText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (selected?.id == store.id) {
                            Text("선택됨", color = fieldBorder, fontSize = 11.sp)
                        }
                    }
                }
                if (filtered.isEmpty()) {
                    Text("일치하는 매장이 없습니다.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    )
}
