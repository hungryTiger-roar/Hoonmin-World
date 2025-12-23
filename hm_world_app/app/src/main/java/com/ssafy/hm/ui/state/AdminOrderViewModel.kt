package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.model.OrderDetail
import com.ssafy.hm.data.model.Orders
import com.ssafy.hm.data.repository.AccountRepository
import com.ssafy.hm.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminOrderState(
    val orders: List<Orders> = emptyList(),
    val orderDetails: Map<Int, List<OrderDetail>> = emptyMap(),
    val accounts: List<Account> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val toast: String? = null
)

class AdminOrderViewModel(
    private val orderRepo: OrderRepository,
    private val accountRepo: AccountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AdminOrderState())
    val state: StateFlow<AdminOrderState> = _state

    fun refresh(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _state.update { it.copy(loading = true) }
            }
            runCatching {
                val orders = orderRepo.getAllOrders()
                val accounts = accountRepo.getAccounts()
                val detailMap = mutableMapOf<Int, List<OrderDetail>>()
                orders.forEach { order ->
                    detailMap[order.orderId] = orderRepo.getOrderDetails(order.orderId)
                }
                Triple(orders, accounts, detailMap)
            }.onSuccess { (orders, accounts, details) ->
                val previousOrders = _state.value.orders.associateBy { it.orderId }
                val mergedOrders = orders.map { order ->
                    val previous = previousOrders[order.orderId]
                    if (previous != null && previous.orderReceived && !order.orderReceived) {
                        order.copy(
                            orderReceived = true,
                            orderReceivedTime = previous.orderReceivedTime
                        )
                    } else {
                        order
                    }
                }
                _state.update {
                    it.copy(
                        orders = mergedOrders,
                        accounts = accounts,
                        orderDetails = details,
                        loading = if (showLoading) false else it.loading,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = if (showLoading) false else it.loading, error = e.message) }
            }
        }
    }

    fun receiveOrder(orderId: Int, customerName: String?) {
        viewModelScope.launch {
            val previousOrders = _state.value.orders
            val receivedAt = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            _state.update { state ->
                val updated = state.orders.map { order ->
                    if (order.orderId == orderId) {
                        order.copy(
                            orderReceived = true,
                            orderReceivedTime = order.orderReceivedTime ?: receivedAt
                        )
                    } else {
                        order
                    }
                }
                state.copy(orders = updated)
            }
            runCatching { orderRepo.receiveOrder(orderId) }
                .onSuccess {
                    val label = customerName?.takeIf { it.isNotBlank() } ?: "고객"
                    _state.update { it.copy(toast = "${label}님 수령 완료 처리되었습니다.", error = null) }
                }
                .onFailure { e ->
                    _state.update { it.copy(orders = previousOrders, error = e.message) }
                }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
