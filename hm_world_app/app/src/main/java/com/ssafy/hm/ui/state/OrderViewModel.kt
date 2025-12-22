package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.OrderCreateRequest
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.OrderDetailPayload
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderState(
    val cart: Map<Item, Int> = emptyMap(),
    val orders: List<Orders> = emptyList(),
    val orderDetails: Map<Int, List<OrderDetail>> = emptyMap(),
    val error: String? = null,
    val toast: String? = null
)

class OrderViewModel(
    private val orderRepo: OrderRepository
) : ViewModel() {
    private var currentUser: String? = null

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state

    fun setUser(userId: String) {
        currentUser = userId
        loadOrders()
    }

    fun addToCart(item: Item) {
        val updated = _state.value.cart.toMutableMap()
        updated[item] = (updated[item] ?: 0) + 1
        _state.update { it.copy(cart = updated, toast = "${item.itemName} 장바구니에 담겼습니다.") }
    }

    fun updateCart(item: Item, qty: Int) {
        val updated = _state.value.cart.toMutableMap()
        if (qty <= 0) updated.remove(item) else updated[item] = qty
        _state.update { it.copy(cart = updated) }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    fun createOrder(store: Int) {
        val user = currentUser ?: return
        val cart = _state.value.cart
        if (cart.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                val details = cart.map { OrderDetailPayload(itemId = it.key.itemId, orderQuantity = it.value) }
                val req = OrderCreateRequest(userId = user, orderStore = store, details = details)
                orderRepo.createOrder(req)
            }.onSuccess {
                _state.update { it.copy(cart = emptyMap(), toast = "주문이 완료되었습니다.") }
                loadOrders()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadOrders() {
        val user = currentUser ?: return
        viewModelScope.launch {
            runCatching {
                val orders = orderRepo.getOrders(user)
                val detailMap = mutableMapOf<Int, List<OrderDetail>>()
                orders.forEach { o ->
                    detailMap[o.orderId] = orderRepo.getOrderDetails(o.orderId)
                }
                Pair(orders, detailMap)
            }.onSuccess { (orders, detailMap) ->
                _state.update { it.copy(orders = orders, orderDetails = detailMap) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun receiveOrder(orderId: Int) {
        viewModelScope.launch {
            runCatching { orderRepo.receiveOrder(orderId) }
                .onSuccess {
                    loadOrders()
                    _state.update { it.copy(toast = "수령 완료 처리되었습니다.") }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            runCatching { orderRepo.deleteOrder(orderId) }
                .onSuccess { loadOrders() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
