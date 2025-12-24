package com.ssafy.hm.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ssafy.hm.ui.navigation.NavRoutes
import com.ssafy.hm.ui.screens.SplashScreen
import com.ssafy.hm.ui.screens.admin.AdminDashboardScreen
import com.ssafy.hm.ui.screens.admin.AdminBuyCarouselListScreen
import com.ssafy.hm.ui.screens.admin.AdminBuyCarouselScreen
import com.ssafy.hm.ui.screens.admin.AdminAttractionEditScreen
import com.ssafy.hm.ui.screens.admin.AdminAttractionListScreen
import com.ssafy.hm.ui.screens.admin.AdminAttractionManagementScreen
import com.ssafy.hm.ui.screens.admin.AdminHomeCarouselListScreen
import com.ssafy.hm.ui.screens.admin.AdminItemEditScreen
import com.ssafy.hm.ui.screens.admin.AdminItemListScreen
import com.ssafy.hm.ui.screens.admin.AdminHomeCarouselScreen
import com.ssafy.hm.ui.screens.admin.AdminHomeManagementScreen
import com.ssafy.hm.ui.screens.admin.AdminNoticeEditScreen
import com.ssafy.hm.ui.screens.admin.AdminNoticeListScreen
import com.ssafy.hm.ui.screens.admin.AdminOrderManagementScreen
import com.ssafy.hm.ui.screens.admin.AdminAttractionLineManagementScreen
import com.ssafy.hm.ui.screens.admin.AdminNotificationManagementScreen
import com.ssafy.hm.ui.screens.auth.LoginScreen
import com.ssafy.hm.ui.screens.auth.SignUpScreen
import com.ssafy.hm.ui.screens.customer.CartScreen
import com.ssafy.hm.ui.screens.customer.CustomerRootScreen
import com.ssafy.hm.ui.screens.customer.OrderDetailScreen
import com.ssafy.hm.ui.screens.customer.AttractionDetailScreen
import com.ssafy.hm.ui.screens.customer.ProductDetailScreen
import com.ssafy.hm.ui.screens.customer.StoreSelectScreen
import com.ssafy.hm.ui.theme.AuroraBlue
import com.ssafy.hm.ui.theme.AuroraGlow
import com.ssafy.hm.ui.theme.AuroraPink
import com.ssafy.hm.ui.theme.AuroraPurple
import com.ssafy.hm.ui.state.AuthViewModel
import com.ssafy.hm.ui.state.AuthViewModelFactory
import com.ssafy.hm.ui.state.AdminOrderViewModel
import com.ssafy.hm.ui.state.AdminOrderViewModelFactory
import com.ssafy.hm.ui.state.AdminAttractionLineViewModel
import com.ssafy.hm.ui.state.AdminAttractionLineViewModelFactory
import com.ssafy.hm.ui.state.CatalogViewModel
import com.ssafy.hm.ui.state.CatalogViewModelFactory
import com.ssafy.hm.ui.state.FriendViewModel
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.ui.state.FriendViewModelFactory
import com.ssafy.hm.ui.state.HomeViewModel
import com.ssafy.hm.ui.state.HomeViewModelFactory
import com.ssafy.hm.ui.state.LineViewModel
import com.ssafy.hm.ui.state.LineViewModelFactory
import com.ssafy.hm.ui.state.OrderViewModel
import com.ssafy.hm.ui.state.OrderViewModelFactory
import com.ssafy.hm.ui.screens.customer.TicketPurchaseScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ssafy.hm.ui.screens.customer.AttractionReservationScreen
import com.ssafy.hm.ui.screens.customer.HomeBoardScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.hm.data.model.FcmTokenRequest
import com.ssafy.hm.data.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HmWorldApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory())
    val catalogVm: CatalogViewModel = viewModel(factory = CatalogViewModelFactory())
    val orderVm: OrderViewModel = viewModel(factory = OrderViewModelFactory())
    val adminOrderVm: AdminOrderViewModel = viewModel(factory = AdminOrderViewModelFactory())
    val adminLineVm: AdminAttractionLineViewModel = viewModel(factory = AdminAttractionLineViewModelFactory())
    val friendVm: FriendViewModel = viewModel(factory = FriendViewModelFactory())
    val lineVm: LineViewModel = viewModel(factory = LineViewModelFactory())

    val authState by authVm.uiState.collectAsState()
    val homeState by homeVm.state.collectAsState()
    val catalogState by catalogVm.state.collectAsState()
    val orderState by orderVm.state.collectAsState()
    val adminOrderState by adminOrderVm.state.collectAsState()
    val adminLineState by adminLineVm.state.collectAsState()
    val friendState by friendVm.state.collectAsState()
    val lineState by lineVm.state.collectAsState()

    var locationPermissionRequested by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        authVm.tryAutoLogin()
        homeVm.refresh()
        catalogVm.refresh()
    }

    LaunchedEffect(authState.account) {
        authState.account?.let { acct ->
            if (!locationPermissionRequested) {
                val fineGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (!fineGranted && !coarseGranted) {
                    locationPermissionRequested = true
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
            orderVm.setUser(acct.userId)
            friendVm.setUser(acct.userId)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            NetworkModule.api.registerFcmToken(FcmTokenRequest(acct.userId, token))
                        }
                    }
                }
            }
            val target = if (acct.userId.equals("staff", ignoreCase = true)) {
                NavRoutes.AdminDashboard.route
            } else {
                NavRoutes.CustomerMain.create(2)
            }
            navController.navigate(target) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState.error) {
        authState.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    LaunchedEffect(authState.registrationCompleted) {
        if (authState.registrationCompleted) {
            Toast.makeText(context, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(homeState.error ?: catalogState.error ?: orderState.error ?: adminOrderState.error ?: adminLineState.error ?: friendState.error ?: lineState.error) {
        (homeState.error ?: catalogState.error ?: orderState.error ?: adminOrderState.error ?: adminLineState.error ?: friendState.error ?: lineState.error)?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(homeState.toast) {
        homeState.toast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            homeVm.clearToast()
        }
    }
    LaunchedEffect(orderState.toast) {
        orderState.toast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            orderVm.clearToast()
        }
    }
    LaunchedEffect(adminOrderState.toast) {
        adminOrderState.toast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            adminOrderVm.clearToast()
        }
    }

    val gradient = Brush.verticalGradient(
        listOf(
            AuroraPurple,
            AuroraGlow,
            AuroraPink.copy(alpha = 0.85f),
            AuroraBlue.copy(alpha = 0.8f)
        )
    )

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        AppNavHost(
            navController = navController,
            authVm = authVm,
            homeVm = homeVm,
            catalogVm = catalogVm,
            orderVm = orderVm,
            adminOrderVm = adminOrderVm,
            adminLineVm = adminLineVm,
            friendVm = friendVm,
            lineVm = lineVm,
            homeState = homeState,
            catalogState = catalogState,
            orderState = orderState,
            adminOrderState = adminOrderState,
            adminLineState = adminLineState,
            friendState = friendState,
            lineState = lineState,
            account = authState.account,
            loading = authState.loading
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    authVm: AuthViewModel,
    homeVm: HomeViewModel,
    catalogVm: CatalogViewModel,
    orderVm: OrderViewModel,
    adminOrderVm: AdminOrderViewModel,
    adminLineVm: AdminAttractionLineViewModel,
    friendVm: FriendViewModel,
    lineVm: LineViewModel,
    homeState: com.ssafy.hm.ui.state.HomeState,
    catalogState: com.ssafy.hm.ui.state.CatalogState,
    orderState: com.ssafy.hm.ui.state.OrderState,
    adminOrderState: com.ssafy.hm.ui.state.AdminOrderState,
    adminLineState: com.ssafy.hm.ui.state.AdminAttractionLineState,
    friendState: com.ssafy.hm.ui.state.FriendState,
    lineState: com.ssafy.hm.ui.state.LineState,
    account: com.ssafy.hm.data.model.Account?,
    loading: Boolean
) {
    val context = LocalContext.current
    // App navigation host
    NavHost(navController = navController, startDestination = NavRoutes.Splash.route) {
        composable(NavRoutes.Splash.route) {
            SplashScreen()
            LaunchedEffect(Unit) {
                // Delay to show splash before login
                kotlinx.coroutines.delay(1000)
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            }
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLogin = { id, pw -> authVm.login(id, pw) },
                onSignUp = { navController.navigate(NavRoutes.SignUp.route) },
                loading = loading
            )
        }
        composable(NavRoutes.SignUp.route) {
            SignUpScreen(
                onBack = { navController.popBackStack() },
                onSubmit = { account -> authVm.register(account) },
                onCheckId = { id -> authVm.isIdAvailable(id) },
                loading = loading
            )
        }
        composable(NavRoutes.AdminDashboard.route) {
            AdminDashboardScreen(
                state = com.ssafy.hm.ui.state.MainUiState(
                    data = com.ssafy.hm.ui.state.HomeData(
                        homeImages = homeState.homeImages,
                        buyImages = homeState.buyImages,
                        boards = homeState.boards,
                        items = catalogState.items,
                        attractions = catalogState.attractions
                    )
                ),
                onRefresh = {
                    homeVm.refresh(); catalogVm.refresh()
                },
                onLogout = {
                    authVm.logout()
                    navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
                },
                onOpenOrderManagement = {
                    navController.navigate(NavRoutes.AdminOrderManagement.route)
                },
                onOpenAttractionManagement = {
                    navController.navigate(NavRoutes.AdminAttractionList.route)
                },
                onOpenHomeManagement = {
                    navController.navigate(NavRoutes.AdminHomeManagement.route)
                },
                onOpenNotificationManagement = {
                    navController.navigate(NavRoutes.AdminNotificationManagement.route)
                }
            )
        }
        composable(NavRoutes.AdminOrderManagement.route) {
            LaunchedEffect(Unit) { adminOrderVm.refresh() }
            AdminOrderManagementScreen(
                orders = adminOrderState.orders,
                orderDetails = adminOrderState.orderDetails,
                accounts = adminOrderState.accounts.associateBy { it.userId },
                items = catalogState.items.associateBy { it.itemId },
                loading = adminOrderState.loading,
                onBack = { navController.popBackStack() },
                onRefresh = { adminOrderVm.refresh() },
                onReceive = { orderId, name -> adminOrderVm.receiveOrder(orderId, name) }
            )
        }
        composable(NavRoutes.AdminNotificationManagement.route) {
            AdminNotificationManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.AdminHomeManagement.route) {
            AdminHomeManagementScreen(
                onBack = { navController.popBackStack() },
                onOpenHomeCarouselList = { navController.navigate(NavRoutes.AdminHomeCarouselList.route) },
                onOpenNoticeList = { navController.navigate(NavRoutes.AdminNoticeList.route) },
                onOpenBuyCarouselList = { navController.navigate(NavRoutes.AdminBuyCarouselList.route) },
                onOpenAttractionList = { navController.navigate(NavRoutes.AdminAttractionManagement.route) },
                onOpenItemList = { navController.navigate(NavRoutes.AdminItemList.route) }
            )
        }
        composable(NavRoutes.AdminHomeCarouselList.route) {
            AdminHomeCarouselListScreen(
                images = homeState.homeImages,
                onBack = { navController.popBackStack() },
                onAddImage = { navController.navigate(NavRoutes.AdminHomeCarousel.route) },
                onDeleteImage = { homeVm.deleteHomeImage(it) }
            )
        }
        composable(NavRoutes.AdminHomeCarousel.route) {
            AdminHomeCarouselScreen(
                onBack = { navController.popBackStack() },
                onUploaded = { homeVm.refresh() }
            )
        }
        composable(NavRoutes.AdminBuyCarouselList.route) {
            AdminBuyCarouselListScreen(
                images = homeState.buyImages,
                onBack = { navController.popBackStack() },
                onAddImage = { navController.navigate(NavRoutes.AdminBuyCarousel.route) },
                onDeleteImage = { homeVm.deleteBuyImage(it) }
            )
        }
        composable(NavRoutes.AdminBuyCarousel.route) {
            AdminBuyCarouselScreen(
                onBack = { navController.popBackStack() },
                onUploaded = { homeVm.refresh() }
            )
        }
        composable(NavRoutes.AdminAttractionManagement.route) {
            AdminAttractionManagementScreen(
                attractions = catalogState.attractions,
                onBack = { navController.popBackStack() },
                onAdd = { navController.navigate(NavRoutes.AdminAttractionCreate.route) },
                onEdit = { navController.navigate(NavRoutes.AdminAttractionEdit.create(it)) },
                onDelete = { catalogVm.deleteAttraction(it) },
                onToggleAble = { catalogVm.toggleAttractionAble(it) }
            )
        }
        composable(NavRoutes.AdminAttractionList.route) {
            AdminAttractionListScreen(
                attractions = catalogState.attractions,
                onBack = { navController.popBackStack() },
                onOpenLine = { navController.navigate(NavRoutes.AdminAttractionLine.create(it)) }
            )
        }
        composable(
            route = NavRoutes.AdminAttractionLine.route,
            arguments = listOf(navArgument("attId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val attId = backStackEntry.arguments?.getInt("attId") ?: 0
            val attraction = catalogState.attractions.firstOrNull { it.attId == attId }
            if (attraction != null) {
                LaunchedEffect(attId) { adminLineVm.load(attId) }
                AdminAttractionLineManagementScreen(
                    attraction = attraction,
                    lines = adminLineState.lines,
                    accounts = adminLineState.accounts.associateBy { it.userId },
                    loading = adminLineState.loading,
                    onBack = { navController.popBackStack() },
                    onRefresh = { adminLineVm.refresh() },
                    onNoShowLine = { adminLineVm.deleteLine(it) },
                    onNoShowMember = { lineId, userId -> adminLineVm.deleteMember(lineId, userId) }
                )
            } else {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "어트랙션 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }
        composable(NavRoutes.AdminAttractionCreate.route) {
            AdminAttractionEditScreen(
                attraction = null,
                onBack = { navController.popBackStack() },
                onSave = { att ->
                    catalogVm.createAttraction(att)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.AdminAttractionEdit.route,
            arguments = listOf(navArgument("attId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("attId") ?: 0
            val attraction = catalogState.attractions.firstOrNull { it.attId == id }
            AdminAttractionEditScreen(
                attraction = attraction,
                onBack = { navController.popBackStack() },
                onSave = { updated ->
                    catalogVm.updateAttraction(id, updated)
                    navController.popBackStack()
                }
            )
        }
        composable(NavRoutes.AdminItemList.route) {
            AdminItemListScreen(
                items = catalogState.items,
                onBack = { navController.popBackStack() },
                onAdd = { navController.navigate(NavRoutes.AdminItemCreate.route) },
                onEdit = { navController.navigate(NavRoutes.AdminItemEdit.create(it)) },
                onDelete = { catalogVm.deleteItem(it) }
            )
        }
        composable(NavRoutes.AdminItemCreate.route) {
            AdminItemEditScreen(
                item = null,
                onBack = { navController.popBackStack() },
                onSave = { item ->
                    catalogVm.createItem(item)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.AdminItemEdit.route,
            arguments = listOf(navArgument("itemId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("itemId") ?: 0
            val item = catalogState.items.firstOrNull { it.itemId == id }
            AdminItemEditScreen(
                item = item,
                onBack = { navController.popBackStack() },
                onSave = { updated ->
                    catalogVm.updateItem(id, updated)
                    navController.popBackStack()
                }
            )
        }
        composable(NavRoutes.AdminNoticeList.route) {
            AdminNoticeListScreen(
                boards = homeState.boards,
                onBack = { navController.popBackStack() },
                onAdd = { navController.navigate(NavRoutes.AdminNoticeCreate.route) },
                onEdit = { navController.navigate(NavRoutes.AdminNoticeEdit.create(it)) },
                onDelete = { homeVm.deleteBoard(it) },
                onRefresh = { homeVm.refresh() }
            )
        }
        composable(NavRoutes.AdminNoticeCreate.route) {
            AdminNoticeEditScreen(
                title = "",
                content = "",
                isEdit = false,
                onBack = { navController.popBackStack() },
                onSave = { title, content ->
                    homeVm.createBoard(title, content)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.AdminNoticeEdit.route,
            arguments = listOf(navArgument("boardId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("boardId") ?: 0
            val board = homeState.boards.firstOrNull { it.boardId == id }
            AdminNoticeEditScreen(
                title = board?.boardTitle ?: "",
                content = board?.boardContent ?: "",
                isEdit = true,
                onBack = { navController.popBackStack() },
                onSave = { title, content ->
                    homeVm.updateBoard(id, title, content)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.CustomerMain.route,
            arguments = listOf(navArgument("tab") {
                type = NavType.IntType
                defaultValue = 2
            })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getInt("tab") ?: 2
            CustomerRootScreen(
                homeState = homeState,
                catalogState = catalogState,
                orderState = orderState,
                friendState = friendState,
                lineState = lineState,

                initialTab = initialTab,

                account = account,
                accounts = friendState.accounts,

                onLogout = {
                    authVm.logout()
                    navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
                },
                onRefresh = { homeVm.refresh(); catalogVm.refresh() },
                onOpenAttractionDetail = { navController.navigate(NavRoutes.AttractionDetail.create(it)) },
                onOpenItemDetail = { navController.navigate(NavRoutes.ItemDetail.create(it)) },
                onOpenCart = { navController.navigate(NavRoutes.Cart.route) },
                onOpenOrderHistory = { navController.navigate(NavRoutes.OrderDetail.route) },
                onAddCart = { orderVm.addToCart(it) },
                onUpdateCart = { item, qty -> orderVm.updateCart(item, qty) },
                onCreateOrder = { orderVm.createOrder(it) },
                onReceiveOrder = { orderVm.receiveOrder(it) },
                onBoardClick = { board: HomeBoard -> navController.navigate(NavRoutes.NoticeDetail.create(board.boardId)) },
                onTicketPurchaseClick = { navController.navigate(NavRoutes.TicketPurchase.route) },
                onReserveAttraction = { attId, userIds -> lineVm.createLine(attId, userIds) {} },
                onAddFriend = { friendVm.addFriend(it) },
                onRemoveFriend = { friendVm.removeFriend(it) },
                onToggleParty = { id, friendId, party -> friendVm.updateFriendParty(id, friendId, party) },
                onRefreshFriends = { friendVm.loadFriends() },
                onRefreshReservation = { lineVm.refreshReservationStatus() },
                onFindReservation = { userId, attIds -> lineVm.findReservation(userId, attIds) },
                clearSelection = { catalogVm.clearSelection() },
                clearToasts = {
                    orderVm.clearToast(); friendVm.clearToast()
                }
            )
        }
        composable(
            route = NavRoutes.NoticeDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getInt("id")
            val board = boardId?.let { homeVm.getBoardById(it) }
            if (board != null) {
                HomeBoardScreen(board = board, onBack = { navController.popBackStack() })
            } else {
                // Handle case where board is not found
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "공지사항을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }
        composable(NavRoutes.TicketPurchase.route) {
            TicketPurchaseScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.Cart.route) {
            CartScreen(
                cart = orderState.cart,
                onChange = { item, qty -> orderVm.updateCart(item, qty) },
                onCheckout = { navController.navigate(NavRoutes.StoreSelect.route) },
                onBack = { navController.popBackStack() },
                onContinueShopping = {
                    navController.navigate(NavRoutes.CustomerMain.create(1)) {
                        popUpTo(NavRoutes.CustomerMain.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.StoreSelect.route) {
            StoreSelectScreen(
                onBack = { navController.popBackStack() },
                onSelectStore = { storeId ->
                    orderVm.createOrder(storeId) {
                        navController.navigate(NavRoutes.OrderDetail.route) {
                            popUpTo(NavRoutes.Cart.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(NavRoutes.OrderDetail.route) {
            OrderDetailScreen(
                orders = orderState.orders,
                orderDetails = orderState.orderDetails,
                items = catalogState.items.associateBy { it.itemId },
                onBack = { navController.popBackStack() },
                onGoShopping = {
                    navController.navigate(NavRoutes.CustomerMain.create(1)) {
                        popUpTo(NavRoutes.CustomerMain.route) { inclusive = true }
                    }
                },
                onRefresh = { orderVm.loadOrders() },
                userId = account?.userId,
                onSubmitReview = { itemId, rating, comment ->
                    catalogVm.addItemReview(itemId, account?.userId, rating, comment)
                },
                onReorder = { order, details ->
                    details.forEach { detail ->
                        val item = catalogState.items.firstOrNull { it.itemId == detail.itemId } ?: return@forEach
                        val current = orderState.cart[item] ?: 0
                        orderVm.updateCart(item, current + detail.orderQuantity)
                    }
                    android.widget.Toast
                        .makeText(context, "상품이 장바구니에 담겼습니다.", android.widget.Toast.LENGTH_SHORT)
                        .show()
                    navController.navigate(NavRoutes.Cart.route)
                }
            )
        }
        composable(
            route = NavRoutes.AttractionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val attId = backStackEntry.arguments?.getInt("id") ?: 0
            LaunchedEffect(attId) { catalogVm.loadAttractionDetail(attId) }
            val attraction = catalogState.selectedAttraction?.takeIf { it.attId == attId }
                ?: catalogState.attractions.firstOrNull { it.attId == attId }
            val userNames = mutableMapOf<String, String>().apply {
                friendState.accounts.forEach { acct ->
                    this[acct.userId] = acct.name ?: acct.userId
                }
                account?.let { acct ->
                    this[acct.userId] = acct.name ?: acct.userId
                }
            }
            AttractionDetailScreen(
                attraction = attraction,
                reviews = catalogState.attractionReviews,
                account = account, // Pass the full account object
                allAttractions = catalogState.attractions,
                userNames = userNames,
                onBack = {
                    catalogVm.clearSelection()
                    navController.popBackStack()
                },
                onReserve = {
                    navController.navigate(NavRoutes.AttractionReservation.create(attId))
                },
                onSubmitReview = { rating, comment ->
                    catalogVm.addAttractionReview(attId, account?.userId, rating, comment)
                },
                onUpdateReview = { reviewId, attId2, rating, comment ->
                    catalogVm.updateAttractionReview(reviewId, attId2, account?.userId, rating, comment)
                },
                onDeleteReview = { reviewId, attId2 ->
                    catalogVm.deleteAttractionReview(reviewId, attId2)
                }
            )
        }
        composable(
            route = NavRoutes.AttractionReservation.route,
            arguments = listOf(navArgument("attId") { type = NavType.IntType })
        ) { backStackEntry ->
            val attId = backStackEntry.arguments?.getInt("attId") ?: 0
            val attraction = catalogState.attractions.firstOrNull { it.attId == attId }
            AttractionReservationScreen(
                attraction = attraction,
                onBack = { navController.popBackStack() },
                onReservationComplete = {
                    Toast.makeText(context, "예약이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoutes.CustomerMain.create(2)) {
                        popUpTo(NavRoutes.CustomerMain.route) { inclusive = true }
                    }
                },
                friendViewModel = friendVm,
                authViewModel = authVm,
                lineViewModel = lineVm
            )
        }
        composable(
            route = NavRoutes.ItemDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("id") ?: 0
            LaunchedEffect(itemId) { catalogVm.loadItemDetail(itemId) }
            val item = catalogState.selectedItem?.takeIf { it.itemId == itemId }
                ?: catalogState.items.firstOrNull { it.itemId == itemId }
            val userNames = mutableMapOf<String, String>().apply {
                friendState.accounts.forEach { acct ->
                    this[acct.userId] = acct.name ?: acct.userId
                }
                account?.let { acct ->
                    this[acct.userId] = acct.name ?: acct.userId
                }
            }
            ProductDetailScreen(
                item = item,
                reviews = catalogState.itemReviews,
                onBack = {
                    catalogVm.clearSelection()
                    navController.popBackStack()
                },
                onAddCart = { orderVm.addToCart(it) },
                onOpenCart = { navController.navigate(NavRoutes.Cart.route) },
                cartCount = orderState.cart.size,
                userId = account?.userId,
                userNames = userNames,
                onUpdateReview = { reviewId, itemId, rating, comment ->
                    catalogVm.updateItemReview(reviewId, itemId, account?.userId, rating, comment)
                },
                onDeleteReview = { reviewId, itemId ->
                    catalogVm.deleteItemReview(reviewId, itemId)
                }
            )
        }
    }
}