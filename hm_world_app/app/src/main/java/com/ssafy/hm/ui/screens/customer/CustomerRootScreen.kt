package com.ssafy.hm.ui.screens.customer

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.navigationBarsPadding
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.ui.state.CatalogState
import com.ssafy.hm.ui.state.FriendState
import com.ssafy.hm.ui.state.HomeState
import com.ssafy.hm.ui.state.LineState
import com.ssafy.hm.ui.state.OrderState
import com.ssafy.hm.data.model.HomeBoard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRootScreen(
    homeState: HomeState,
    catalogState: CatalogState,
    orderState: OrderState,
    friendState: FriendState,
    lineState: LineState,
    initialTab: Int,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onLoadAttraction: (Int) -> Unit,
    onOpenItemDetail: (Int) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrderHistory: () -> Unit,
    onAddCart: (Item) -> Unit,
    onUpdateCart: (Item, Int) -> Unit,
    onCreateOrder: (Int) -> Unit,
    onReceiveOrder: (Int) -> Unit,
    onBoardClick: (HomeBoard) -> Unit,
    onTicketPurchaseClick: () -> Unit,
    onReserveAttraction: (Int, List<String>) -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (Int) -> Unit,
    clearSelection: () -> Unit,
    clearToasts: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var showChat by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(orderState.error ?: friendState.error ?: lineState.error) {
        (orderState.error ?: friendState.error ?: lineState.error)?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(friendState.toast ?: lineState.toast) {
        (friendState.toast ?: lineState.toast)?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            clearToasts()
        }
    }

    val navItems = listOf("\uC5B4\uD2B8\uB799\uC158 \uC608\uC57D", "\uC0C1\uD488 \uAD6C\uB9E4", "\uD648", "\uC9C0\uB3C4", "\uB0B4 \uC815\uBCF4")
    val navIcons = listOf(Icons.Default.Info, Icons.Default.AddShoppingCart, Icons.Default.Info, Icons.Default.Map, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                navItems.forEachIndexed { idx, label ->
                    NavigationBarItem(
                        selected = tab == idx,
                        onClick = { tab = idx },
                        icon = { Icon(navIcons[idx], contentDescription = label) },
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
    ) { innerPadding ->
        when (tab) {
            0 -> AttractionTab(catalogState.attractions, onLoadAttraction, innerPadding)
            1 -> ProductTab(
                list = catalogState.items,
                buyImages = catalogState.buyImages,
                onSelect = onOpenItemDetail,
                onAddCart = onAddCart,
                onOpenCart = onOpenCart,
                onOpenOrderHistory = onOpenOrderHistory,
                paddingValues = innerPadding
            )
            2 -> HomeTab(
                images = homeState.homeImages,
                boards = homeState.boards,
                orderState = orderState,
                paddingValues = innerPadding,
                onBoardClick = onBoardClick,
                onTicketPurchaseClick = onTicketPurchaseClick
            )
            3 -> MapTab(innerPadding)
            else -> ProfileTab(
                orders = orderState.orders,
                details = orderState.orderDetails,
                onReceive = onReceiveOrder,
                friends = friendState.friends,
                availableFriends = friendState.availableFriends,
                onAddFriend = onAddFriend,
                onRemoveFriend = onRemoveFriend,
                onLogout = onLogout,
                paddingValues = innerPadding
            )
        }
        catalogState.selectedAttraction?.let {
            AttractionDetailDialog(it, catalogState.attractionReviews, onReserve = {
                onReserveAttraction(it.attId, friendState.availableFriends.map { f -> f.friend.friendId })
            }) { clearSelection() }
        }
        if (showChat) ChatbotOverlay { showChat = false }
    }
}






