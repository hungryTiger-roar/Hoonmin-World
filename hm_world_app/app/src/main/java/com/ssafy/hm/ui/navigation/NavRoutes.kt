package com.ssafy.hm.ui.navigation

sealed class NavRoutes(val route: String) {
    // 앱 시작 시 잠깐 보여줄 스플래시 화면
    data object Splash : NavRoutes("splash")
    data object Login : NavRoutes("login")
    data object SignUp : NavRoutes("signup")
    data object AdminDashboard : NavRoutes("admin/dashboard")
    data object AdminOrderManagement : NavRoutes("admin/order-management")
    data object AdminHomeManagement : NavRoutes("admin/home-management")
    data object AdminNotificationManagement : NavRoutes("admin/notification-management")
    data object AdminHomeCarouselList : NavRoutes("admin/home-carousel/list")
    data object AdminHomeCarousel : NavRoutes("admin/home-carousel")
    data object AdminAttractionManagement : NavRoutes("admin/attraction/management")
    data object AdminAttractionList : NavRoutes("admin/attraction/list")
    data object AdminAttractionLine : NavRoutes("admin/attraction/line/{attId}") {
        fun create(attId: Int) = "admin/attraction/line/$attId"
    }
    data object AdminAttractionEdit : NavRoutes("admin/attraction/edit/{attId}") {
        fun create(attId: Int) = "admin/attraction/edit/$attId"
    }
    data object AdminAttractionCreate : NavRoutes("admin/attraction/create")
    data object AdminItemList : NavRoutes("admin/item/list")
    data object AdminItemEdit : NavRoutes("admin/item/edit/{itemId}") {
        fun create(itemId: Int) = "admin/item/edit/$itemId"
    }
    data object AdminItemCreate : NavRoutes("admin/item/create")
    data object AdminBuyCarouselList : NavRoutes("admin/buy-carousel/list")
    data object AdminBuyCarousel : NavRoutes("admin/buy-carousel")
    data object AdminNoticeList : NavRoutes("admin/notice/list")
    data object AdminNoticeCreate : NavRoutes("admin/notice/create")
    data object AdminNoticeEdit : NavRoutes("admin/notice/edit/{boardId}") {
        fun create(boardId: Int) = "admin/notice/edit/$boardId"
    }
    data object CustomerMain : NavRoutes("customer/main")
    data object TicketPurchase : NavRoutes("customer/ticket-purchase")
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
