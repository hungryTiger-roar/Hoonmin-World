package com.ssafy.hm.data.network

import com.ssafy.hm.data.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HmApi {

    @POST("/accounts/login")
    suspend fun login(@Body payload: Map<String, String>): Account

    @POST("/accounts")
    suspend fun register(@Body account: Account): Account

    @GET("/accounts/{userId}")
    suspend fun getAccount(@Path("userId") userId: String): Account

    @GET("/accounts")
    suspend fun getAccounts(): List<Account>

    @GET("/attractions")
    suspend fun getAttractions(): List<Attraction>

    @GET("/attractions/{attId}")
    suspend fun getAttraction(@Path("attId") attId: Int): Attraction

    @GET("/items")
    suspend fun getItems(): List<Item>

    @GET("/items/{itemId}")
    suspend fun getItem(@Path("itemId") itemId: Int): Item

    @GET("/home-images")
    suspend fun getHomeImages(): List<HomeImage>

    @GET("/buy-images")
    suspend fun getBuyImages(): List<BuyImage>

    @POST("/buy-images")
    suspend fun createBuyImage(@Body image: BuyImageRequest): BuyImage

    @DELETE("/buy-images/{buyId}")
    suspend fun deleteBuyImage(@Path("buyId") buyId: Int): Void

    @POST("/home-images")
    suspend fun createHomeImage(@Body image: HomeImage): HomeImage

    @DELETE("/home-images/{homeId}")
    suspend fun deleteHomeImage(@Path("homeId") homeId: Int): Void

    @GET("/board")
    suspend fun getBoards(): List<HomeBoard>

    @GET("/board/{boardId}")
    suspend fun getBoard(@Path("boardId") boardId: Int): HomeBoard

    @POST("/board")
    suspend fun createBoard(@Body board: HomeBoardWriteRequest): Void

    @PATCH("/board/{boardId}")
    suspend fun updateBoard(@Path("boardId") boardId: Int, @Body board: HomeBoardWriteRequest): Void

    @DELETE("/board/{boardId}")
    suspend fun deleteBoard(@Path("boardId") boardId: Int): Void

    @GET("/reviews/items/{itemId}")
    suspend fun getItemReviews(@Path("itemId") itemId: Int): List<ItemReview>

    @GET("/reviews/attractions/{attId}")
    suspend fun getAttractionReviews(@Path("attId") attId: Int): List<AttractionReview>

    @POST("/reviews/items")
    suspend fun addItemReview(@Body review: ItemReview): ItemReview

    @POST("/reviews/attractions")
    suspend fun addAttractionReview(@Body review: AttractionReview): AttractionReview

    @PATCH("/reviews/items/review/{itemReviewId}")
    suspend fun updateItemReview(@Path("itemReviewId") id: Int, @Body review: ItemReview): Void

    @PATCH("/reviews/attractions/review/{attReviewId}")
    suspend fun updateAttractionReview(@Path("attReviewId") id: Int, @Body review: AttractionReview): Void

    @DELETE("/reviews/items/review/{itemReviewId}")
    suspend fun deleteItemReview(@Path("itemReviewId") id: Int): Void

    @DELETE("/reviews/attractions/review/{attReviewId}")
    suspend fun deleteAttractionReview(@Path("attReviewId") id: Int): Void

    @GET("/orders/user/{userId}")
    suspend fun getOrders(@Path("userId") userId: String): List<Orders>

    @GET("/orders")
    suspend fun getAllOrders(): List<Orders>

    @GET("/orders/{orderId}/details")
    suspend fun getOrderDetails(@Path("orderId") orderId: Int): List<OrderDetail>

    @POST("/orders")
    suspend fun createOrder(@Body request: OrderCreateRequest): Int

    @PATCH("/orders/{orderId}/receive")
    suspend fun receiveOrder(@Path("orderId") orderId: Int): Void

    @DELETE("/orders/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId: Int): Void

    @GET("/friends/user/{userId}")
    suspend fun getFriends(@Path("userId") userId: String): List<Friend>

    @GET("/friends/user/{userId}/ticket/available")
    suspend fun getFriendsWithTicketAvailable(@Path("userId") userId: String): List<Friend>

    @POST("/friends")
    suspend fun addFriend(@Body friend: Friend): Friend

    @DELETE("/friends/{id}")
    suspend fun removeFriend(@Path("id") id: Int): Void

    @POST("/friends/{id}/party")
    suspend fun updateFriendPartyPost(@Path("id") id: Int, @Query("friendParty") friendParty: Boolean): Void

    @PATCH("/friends/{id}/party")
    suspend fun updateFriendPartyPatch(@Path("id") id: Int, @Query("friendParty") friendParty: Boolean): Void

    @POST("/friends/party")
    suspend fun updateFriendPartyByIds(
        @Query("userId") userId: String,
        @Query("friendId") friendId: String,
        @Query("friendParty") friendParty: Boolean
    ): Void

    @POST("/line/{attId}")
    suspend fun createLine(@Path("attId") attId: Int, @Body req: AttractionLineCreateRequest): Int

    @GET("/line/attraction/{attId}")
    suspend fun getLinesByAttraction(@Path("attId") attId: Int): List<AttractionLine>

    @GET("/line/{lineId}/members")
    suspend fun getLineMembers(@Path("lineId") lineId: Int): List<AttractionLineMember>

    @DELETE("/line/{lineId}/member/{userId}")
    suspend fun deleteLineMember(@Path("lineId") lineId: Int, @Path("userId") userId: String): Void

    @DELETE("/line/{lineId}")
    suspend fun deleteLine(@Path("lineId") lineId: Int): Void

    // Admin item/attraction management (optional CRUD)
    @POST("/items")
    suspend fun createItem(@Body item: Item): Item

    @PUT("/items/{itemId}")
    suspend fun updateItem(@Path("itemId") itemId: Int, @Body item: Item): Item

    @DELETE("/items/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Int): Void

    @POST("/attractions")
    suspend fun createAttraction(@Body attraction: Attraction): Attraction

    @PUT("/attractions/{attId}")
    suspend fun updateAttraction(@Path("attId") attId: Int, @Body attraction: Attraction): Attraction

    @PATCH("/attractions/{attId}/able")
    suspend fun toggleAttractionAble(@Path("attId") attId: Int): Void

    @DELETE("/attractions/{attId}")
    suspend fun deleteAttraction(@Path("attId") attId: Int): Void

    @Multipart
    @POST("/upload")
    suspend fun uploadImage(@Part upload_file: MultipartBody.Part): ResponseBody

    @POST("/notifications/token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Void

    @POST("/notifications/send")
    suspend fun sendNotification(@Body request: NotificationSendRequest): Response<Unit>

    @POST("/notifications/schedule")
    suspend fun scheduleNotification(@Body request: NotificationScheduleRequest): Response<Unit>

    @POST("/notifications/repeat")
    suspend fun repeatNotification(@Body request: NotificationRepeatRequest): Response<Unit>

    @GET("/notifications/scheduled")
    suspend fun getScheduledNotifications(): List<PushNotification>

    @GET("/notifications/repeat")
    suspend fun getRepeatNotifications(): List<PushNotification>

    @PUT("/notifications/{id}")
    suspend fun updateNotification(@Path("id") id: Int, @Body request: NotificationUpdateRequest): Response<Unit>

    @DELETE("/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): Response<Unit>
}
