package com.ssafy.hm.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.hm.ui.navigation.NavRoutes
import com.ssafy.hm.ui.screens.admin.AdminDashboardScreen
import com.ssafy.hm.ui.screens.auth.LoginScreen
import com.ssafy.hm.ui.screens.auth.SignUpScreen
import com.ssafy.hm.ui.screens.customer.CustomerRootScreen
import com.ssafy.hm.ui.theme.AuroraBlue
import com.ssafy.hm.ui.theme.AuroraGlow
import com.ssafy.hm.ui.theme.AuroraPink
import com.ssafy.hm.ui.theme.AuroraPurple
import com.ssafy.hm.ui.state.AuthViewModel
import com.ssafy.hm.ui.state.AuthViewModelFactory
import com.ssafy.hm.ui.state.CatalogViewModel
import com.ssafy.hm.ui.state.CatalogViewModelFactory
import com.ssafy.hm.ui.state.FriendViewModel
import com.ssafy.hm.ui.state.FriendViewModelFactory
import com.ssafy.hm.ui.state.HomeViewModel
import com.ssafy.hm.ui.state.HomeViewModelFactory
import com.ssafy.hm.ui.state.LineViewModel
import com.ssafy.hm.ui.state.LineViewModelFactory
import com.ssafy.hm.ui.state.OrderViewModel
import com.ssafy.hm.ui.state.OrderViewModelFactory

@Composable
fun HmWorldApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory())
    val catalogVm: CatalogViewModel = viewModel(factory = CatalogViewModelFactory())
    val orderVm: OrderViewModel = viewModel(factory = OrderViewModelFactory())
    val friendVm: FriendViewModel = viewModel(factory = FriendViewModelFactory())
    val lineVm: LineViewModel = viewModel(factory = LineViewModelFactory())

    val authState by authVm.uiState.collectAsState()
    val homeState by homeVm.state.collectAsState()
    val catalogState by catalogVm.state.collectAsState()
    val orderState by orderVm.state.collectAsState()
    val friendState by friendVm.state.collectAsState()
    val lineState by lineVm.state.collectAsState()

    LaunchedEffect(Unit) {
        authVm.tryAutoLogin()
        homeVm.refresh()
        catalogVm.refresh()
    }

    LaunchedEffect(authState.account) {
        authState.account?.let { acct ->
            orderVm.setUser(acct.userId)
            friendVm.setUser(acct.userId)
            val target = if (acct.userId.equals("staff", ignoreCase = true)) {
                NavRoutes.AdminDashboard.route
            } else {
                NavRoutes.CustomerMain.route
            }
            navController.navigate(target) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
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
    LaunchedEffect(homeState.error ?: catalogState.error ?: orderState.error ?: friendState.error ?: lineState.error) {
        (homeState.error ?: catalogState.error ?: orderState.error ?: friendState.error ?: lineState.error)?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
            friendVm = friendVm,
            lineVm = lineVm,
            homeState = homeState,
            catalogState = catalogState,
            orderState = orderState,
            friendState = friendState,
            lineState = lineState,
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
    friendVm: FriendViewModel,
    lineVm: LineViewModel,
    homeState: com.ssafy.hm.ui.state.HomeState,
    catalogState: com.ssafy.hm.ui.state.CatalogState,
    orderState: com.ssafy.hm.ui.state.OrderState,
    friendState: com.ssafy.hm.ui.state.FriendState,
    lineState: com.ssafy.hm.ui.state.LineState,
    loading: Boolean
) {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = NavRoutes.Login.route) {
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
                }
            )
        }
        composable(NavRoutes.CustomerMain.route) {
            CustomerRootScreen(
                homeState = homeState,
                catalogState = catalogState,
                orderState = orderState,
                friendState = friendState,
                lineState = lineState,
                onLogout = {
                    authVm.logout()
                    navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
                },
                onRefresh = { homeVm.refresh(); catalogVm.refresh() },
                onLoadAttraction = { catalogVm.loadAttractionDetail(it) },
                onLoadItem = { catalogVm.loadItemDetail(it) },
                onAddCart = { orderVm.addToCart(it) },
                onUpdateCart = { item, qty -> orderVm.updateCart(item, qty) },
                onCreateOrder = { orderVm.createOrder(it) },
                onReceiveOrder = { orderVm.receiveOrder(it) },
                onDeleteBoard = { homeVm.deleteBoard(it) },
                onReserveAttraction = { attId, userIds -> lineVm.createLine(attId, userIds) },
                onAddFriend = { friendVm.addFriend(it) },
                onRemoveFriend = { friendVm.removeFriend(it) },
                clearSelection = { catalogVm.clearSelection() },
                clearToasts = {
                    orderVm.clearToast(); friendVm.clearToast()
                }
            )
        }
    }
}
