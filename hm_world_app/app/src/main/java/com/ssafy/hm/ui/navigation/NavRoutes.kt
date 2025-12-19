package com.ssafy.hm.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object Login : NavRoutes("login")
    data object SignUp : NavRoutes("signup")
    data object AdminDashboard : NavRoutes("admin/dashboard")
    data object AdminHomeManagement : NavRoutes("admin/home-management")
    data object AdminHomeCarouselList : NavRoutes("admin/home-carousel/list")
    data object AdminHomeCarousel : NavRoutes("admin/home-carousel")
    data object AdminAttractionList : NavRoutes("admin/attraction/list")
    data object AdminAttractionEdit : NavRoutes("admin/attraction/edit/{attId}") {
        fun create(attId: Int) = "admin/attraction/edit/$attId"
    }
    data object AdminAttractionCreate : NavRoutes("admin/attraction/create")
    data object AdminBuyCarouselList : NavRoutes("admin/buy-carousel/list")
    data object AdminBuyCarousel : NavRoutes("admin/buy-carousel")
    data object AdminNoticeList : NavRoutes("admin/notice/list")
    data object AdminNoticeCreate : NavRoutes("admin/notice/create")
    data object AdminNoticeEdit : NavRoutes("admin/notice/edit/{boardId}") {
        fun create(boardId: Int) = "admin/notice/edit/$boardId"
    }
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
