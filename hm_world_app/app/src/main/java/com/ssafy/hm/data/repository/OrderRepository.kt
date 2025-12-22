package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.OrderCreateRequest
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.data.network.HmApi

class OrderRepository(private val api: HmApi) {
    suspend fun getOrders(userId: String): List<Orders> = api.getOrders(userId)
    suspend fun getAllOrders(): List<Orders> = api.getAllOrders()
    suspend fun getOrderDetails(orderId: Int): List<OrderDetail> = api.getOrderDetails(orderId)
    suspend fun createOrder(request: OrderCreateRequest): Int = api.createOrder(request)
    suspend fun receiveOrder(orderId: Int) = api.receiveOrder(orderId)
    suspend fun deleteOrder(orderId: Int) = api.deleteOrder(orderId)
}
