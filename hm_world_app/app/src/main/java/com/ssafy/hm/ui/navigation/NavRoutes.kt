package com.ssafy.hm.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object Login : NavRoutes("login")
    data object SignUp : NavRoutes("signup")
    data object AdminDashboard : NavRoutes("admin/dashboard")
    data object CustomerMain : NavRoutes("customer/main")
    data object NoticeDetail : NavRoutes("notice/{id}") {
        fun create(id: Int) = "notice/$id"
    }
    data object ItemDetail : NavRoutes("item/{id}") {
        fun create(id: Int) = "item/$id"
    }
    data object AttractionDetail : NavRoutes("att/{id}") {
        fun create(id: Int) = "att/$id"
    }
}
