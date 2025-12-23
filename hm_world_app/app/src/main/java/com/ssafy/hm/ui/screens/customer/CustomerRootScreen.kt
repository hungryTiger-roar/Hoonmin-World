package com.ssafy.hm.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ssafy.hm.R
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.ui.state.CatalogState
import com.ssafy.hm.ui.state.FriendState
import com.ssafy.hm.ui.state.HomeState
import com.ssafy.hm.ui.state.LineState
import com.ssafy.hm.ui.state.OrderState
import com.ssafy.hm.ui.theme.AuroraGlow
import com.ssafy.hm.ui.theme.AuroraMist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRootScreen(
    homeState: HomeState,
    catalogState: CatalogState,
    orderState: OrderState,
    friendState: FriendState,
    lineState: LineState,
    initialTab: Int,
    account: Account?,
    accounts: List<Account>,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onOpenAttractionDetail: (Int) -> Unit,
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
    onToggleParty: (Int, String, Boolean) -> Unit,
    onRefreshFriends: () -> Unit,
    clearSelection: () -> Unit,
    clearToasts: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var showChat by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(tab) {
        if (tab == 4) onRefreshFriends()
    }

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

    val navItems = listOf("어트랙션 예약", "상품 구매", "홈", "지도", "내정보")
    val navIconRes = listOf(
        R.drawable.icons8_50,
        R.drawable.icons8_32,
        R.drawable.homeicon_32
    )

    Scaffold(
        bottomBar = {
            val navBarHeight = 56.dp
            val homeOuterSize = 72.dp
            val homeLift = 20.dp
            val homeShadow = 12.dp
            val homePressedShadow = 20.dp
            val homeInteraction = remember { MutableInteractionSource() }
            val isHomePressed by homeInteraction.collectIsPressedAsState()
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(navBarHeight)
                    .fillMaxWidth()
                    .graphicsLayer { clip = false }
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navBarHeight)
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { clip = false }
                ) {
                    navItems.forEachIndexed { idx, label ->
                        NavigationBarItem(
                            selected = tab == idx,
                            onClick = { tab = idx },
                            icon = {
                                val iconTint = LocalContentColor.current
                                val otherIconSize = 24.dp
                                when (idx) {
                                    0, 1 -> Icon(
                                        painter = painterResource(id = navIconRes[idx]),
                                        contentDescription = label,
                                        modifier = Modifier.size(otherIconSize),
                                        tint = iconTint
                                    )
                                    2 -> Box(modifier = Modifier.size(homeOuterSize))
                                    3 -> Icon(
                                        Icons.Default.Map,
                                        contentDescription = label,
                                        modifier = Modifier.size(otherIconSize),
                                        tint = iconTint
                                    )
                                    else -> Icon(
                                        Icons.Default.Person,
                                        contentDescription = label,
                                        modifier = Modifier.size(otherIconSize),
                                        tint = iconTint
                                    )
                                }
                            },
                            label = {},
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuroraGlow,
                                selectedTextColor = AuroraGlow,
                                unselectedIconColor = Color.Black,
                                unselectedTextColor = Color.Black,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                val isSelected = tab == 2
                val bgColor = if (isSelected) AuroraMist else Color.White
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -homeLift)
                        .size(homeOuterSize)
                        .shadow(
                            if (isHomePressed) homePressedShadow else homeShadow,
                            CircleShape
                        )
                        .background(bgColor, CircleShape)
                        .zIndex(1f)
                        .clickable(
                            interactionSource = homeInteraction,
                            indication = null
                        ) { tab = 2 },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = navIconRes[2]),
                        contentDescription = navItems[2],
                        tint = Color.Unspecified
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
            0 -> AttractionTab(catalogState.attractions, onOpenAttractionDetail, innerPadding)
            1 -> ProductTab(
                list = catalogState.items,
                buyImages = catalogState.buyImages,
                onSelect = onOpenItemDetail,
                onAddCart = onAddCart,
                onOpenCart = onOpenCart,
                cartCount = orderState.cart.size,
                onOpenOrderHistory = onOpenOrderHistory,
                paddingValues = innerPadding
            )
            2 -> HomeTab(
                images = homeState.homeImages,
                boards = homeState.boards,
                hasTicket = (account?.ticket == true),
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
                accounts = accounts,
                onAddFriend = onAddFriend,
                onRemoveFriend = onRemoveFriend,
                onToggleParty = onToggleParty,
                onLogout = onLogout,
                paddingValues = innerPadding,
                account = account
            )
        }

        if (showChat) ChatbotOverlay { showChat = false }
    }
}





