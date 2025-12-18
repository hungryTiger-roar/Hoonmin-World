package com.ssafy.hm.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionReview
import com.ssafy.hm.data.model.Friend
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.ItemReview
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.ui.state.CatalogState
import com.ssafy.hm.ui.state.FriendState
import com.ssafy.hm.ui.state.HomeState
import com.ssafy.hm.ui.state.LineState
import com.ssafy.hm.ui.state.OrderState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRootScreen(
    homeState: HomeState,
    catalogState: CatalogState,
    orderState: OrderState,
    friendState: FriendState,
    lineState: LineState,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onLoadAttraction: (Int) -> Unit,
    onLoadItem: (Int) -> Unit,
    onAddCart: (Item) -> Unit,
    onUpdateCart: (Item, Int) -> Unit,
    onCreateOrder: (Int) -> Unit,
    onReceiveOrder: (Int) -> Unit,
    onDeleteBoard: (Int) -> Unit,
    onReserveAttraction: (Int, List<String>) -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (Int) -> Unit,
    clearSelection: () -> Unit,
    clearToasts: () -> Unit
) {
    var tab by remember { mutableStateOf(2) }
    var showCart by remember { mutableStateOf(false) }
    var showChat by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(orderState.error ?: friendState.error ?: lineState.error) {
        (orderState.error ?: friendState.error ?: lineState.error)?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(orderState.toast ?: friendState.toast ?: lineState.toast) {
        (orderState.toast ?: friendState.toast ?: lineState.toast)?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            clearToasts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("훈민월드") },
                actions = {
                    IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, contentDescription = "refresh") }
                    IconButton(onClick = { showCart = true }) { Icon(Icons.Default.AddShoppingCart, contentDescription = "cart") }
                    TextButton(onClick = onLogout) { Text("로그아웃", color = Color(0xFF6A5AE0)) }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf("어트랙션", "상품", "홈", "지도", "내정보")
                val icons = listOf(Icons.Default.Info, Icons.Default.AddShoppingCart, Icons.Default.Info, Icons.Default.Map, Icons.Default.Person)
                items.forEachIndexed { idx, label ->
                    NavigationBarItem(
                        selected = tab == idx,
                        onClick = { tab = idx },
                        icon = { Icon(icons[idx], contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (tab == 2) {
                FloatingActionButton(onClick = { showChat = true }, containerColor = Color(0xFF6A5AE0)) {
                    Icon(Icons.Default.Chat, contentDescription = "chat")
                }
            }
        }
    ) { inner ->
        when (tab) {
            0 -> AttractionTab(catalogState.attractions, onLoadAttraction)
            1 -> ProductTab(catalogState.items, onLoadItem, onAddCart)
            2 -> HomeTab(
                images = homeState.homeImages,
                boards = homeState.boards,
                orderState = orderState,
                lineState = lineState,
                onDeleteBoard = onDeleteBoard
            )
            3 -> MapTab()
            else -> ProfileTab(
                orders = orderState.orders,
                details = orderState.orderDetails,
                onReceive = onReceiveOrder,
                friends = friendState.friends,
                availableFriends = friendState.availableFriends,
                onAddFriend = onAddFriend,
                onRemoveFriend = onRemoveFriend,
                onLogout = onLogout
            )
        }

        if (showCart) {
            CartDialog(
                cart = orderState.cart,
                onChange = onUpdateCart,
                onOrder = { store ->
                    onCreateOrder(store)
                    showCart = false
                },
                onDismiss = { showCart = false }
            )
        }
        catalogState.selectedItem?.let {
            ItemDetailDialog(it, catalogState.itemReviews, onAddCart = { onAddCart(it) }) {
                clearSelection()
            }
        }
        catalogState.selectedAttraction?.let {
            AttractionDetailDialog(it, catalogState.attractionReviews, onReserve = {
                onReserveAttraction(it.attId, friendState.availableFriends.map { f -> f.friendId })
            }) { clearSelection() }
        }
        if (showChat) ChatbotOverlay { showChat = false }
    }
}

@Composable
private fun HomeTab(
    images: List<HomeImage>,
    boards: List<HomeBoard>,
    orderState: OrderState,
    lineState: LineState,
    onDeleteBoard: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { maxOf(images.size, 1) })

    // 티켓/예약 상태 예시
    val hasTicket = orderState.orders.isNotEmpty()
    val firstLine = lineState.lineMembers.values.firstOrNull()
    val waitingCount = firstLine?.size ?: 0
    val expectedMinutes = if (waitingCount == 0) 0 else ((waitingCount - 1) / 20 + 1) * 10

    // 캐러셀 자동 슬라이드
    LaunchedEffect(pagerState.pageCount) {
        while (pagerState.pageCount > 1) {
            kotlinx.coroutines.delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8E6FF), Color.White)
                )
            )
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 캐러셀 (상단)
        Text("홈 캐러셀", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5A4FD3))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            HorizontalPager(state = pagerState) { page ->
                val image = images.getOrNull(page)
                if (image != null) {
                    AsyncImage(
                        model = image.homeImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEFEFFF)),
                        contentAlignment = Alignment.Center
                    ) { Text("이미지 준비 중") }
                }
            }
        }

        // 상단 인사/티켓 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A5AE0).copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("훈민월드에 오신 것을 환영합니다", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A3FB6))
                if (hasTicket) {
                    Text("행복 가득한 하루 보내세요!", fontWeight = FontWeight.Bold, color = Color(0xFF6A5AE0))
                } else {
                    Text("현재 구매한 티켓이 없습니다. 탭하여 티켓 구매하기", color = Color.DarkGray)
                    Button(
                        onClick = { /* TODO: 구매 화면으로 네비게이션 */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("티켓 구매하기") }
                }
                if (waitingCount > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("예약 대기", fontWeight = FontWeight.Bold)
                            Text("예상 대기시간: ${expectedMinutes}분", color = Color(0xFF6A5AE0))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { /* TODO: 예약 취소 다이얼로그 */ }) { Text("예약 취소") }
                                Button(onClick = { /* TODO: 단체 예약 취소 */ }) { Text("단체 예약 취소") }
                            }
                        }
                    }
                }
            }
        }

        // 공지 목록만 표시 (등록은 관리자)
        Text("공지사항", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        boards.forEach { board ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(board.boardTitle ?: "", fontWeight = FontWeight.Bold)
                    }
                    Text(board.boardContent ?: "", maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(board.boardDate ?: "", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AttractionTab(list: List<Attraction>, onSelect: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("어트랙션 검색") }
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list.filter { it.attName.contains(query, true) }) { att ->
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
                            model = att.attPic,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(att.attName ?: "", fontWeight = FontWeight.Bold)
                            Text(att.attComment ?: "", maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("최대 ${att.attCapacity}명 / 총 탑승 ${att.attTotal}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductTab(list: List<Item>, onSelect: (Int) -> Unit, onAddCart: (Item) -> Unit) {
    var query by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("상품 검색") }
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list.filter { query.isBlank() || it.itemName.contains(query, true) || (it.itemCategory?.contains(query, true) == true) }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                        .clickable { onSelect(item.itemId) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = item.itemPic,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.weight(1f)) {
                            Text(item.itemName ?: "", fontWeight = FontWeight.Bold)
                            Text("${item.itemPrice}원")
                            Text(item.itemCategory ?: "", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { onAddCart(item) }) { Icon(Icons.Default.AddShoppingCart, contentDescription = "cart") }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF1F5)),
        contentAlignment = Alignment.Center
    ) { Text("지도 API 연동 예정", color = Color.Gray) }
}

@Composable
private fun ProfileTab(
    orders: List<Orders>,
    details: Map<Int, List<OrderDetail>>,
    onReceive: (Int) -> Unit,
    friends: List<Friend>,
    availableFriends: List<Friend>,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var friendSearch by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("내 정보", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("주문 내역", fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 240.dp)) {
            if (orders.isEmpty()) {
                item { Text("주문 내역이 없습니다.") }
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
            items(friends.filter { it.friendId.contains(friendSearch, true) }) { fr ->
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
        Text("줄서기 일행(오늘 티켓/예약 없음)", fontSize = 12.sp, color = Color.Gray)
        FlowFriends(availableFriends)

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
            Text("로그아웃")
        }
    }
}

@Composable
private fun FlowFriends(friends: List<Friend>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        friends.forEach {
            Text(
                text = it.friendId,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8E8F5))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun CartDialog(
    cart: Map<Item, Int>,
    onChange: (Item, Int) -> Unit,
    onOrder: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var store by remember { mutableStateOf("1") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val storeId = store.toIntOrNull() ?: 1
                onOrder(storeId)
            }) { Text("주문") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } },
        title = { Text("장바구니") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (cart.isEmpty()) {
                    Text("장바구니가 비었습니다.")
                } else {
                    cart.forEach { (item, qty) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.itemName)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onChange(item, qty - 1) }) { Text("-") }
                                Text("$qty")
                                IconButton(onClick = { onChange(item, qty + 1) }) { Text("+") }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = store,
                    onValueChange = { store = it },
                    label = { Text("수령 매장 번호 (1/2/3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}

@Composable
private fun ItemDetailDialog(
    item: Item,
    reviews: List<ItemReview>,
    onAddCart: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onAddCart) { Text("장바구니") }
                TextButton(onClick = onDismiss) { Text("닫기") }
            }
        },
        title = { Text(item.itemName ?: "") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("가격: ${item.itemPrice}원")
                Text(item.itemComment ?: "")
                Text("리뷰 (${reviews.size})", fontWeight = FontWeight.Bold)
                reviews.forEach { r -> Text("- ${r.itemReviewComment ?: ""} (${r.itemRating}점)") }
            }
        }
    )
}

@Composable
private fun AttractionDetailDialog(
    att: Attraction,
    reviews: List<AttractionReview>,
    onReserve: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onReserve) { Text("예약/줄서기") }
                TextButton(onClick = onDismiss) { Text("닫기") }
            }
        },
        title = { Text(att.attName ?: "") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(att.attComment ?: "")
                Text("최대 탑승: ${att.attCapacity}명, 총 탑승: ${att.attTotal}")
                Text("리뷰 (${reviews.size})", fontWeight = FontWeight.Bold)
                reviews.forEach { r -> Text("- ${r.attReviewComment ?: ""} (${r.attRating}점)") }
            }
        }
    )
}

@Composable
private fun ChatbotOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("챗봇", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("챗봇 화면만 표시합니다. (기능 미구현)")
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("닫기") }
            }
        }
    }
}
