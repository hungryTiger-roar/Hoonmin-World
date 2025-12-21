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

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching {
                val orders = orderRepo.getAllOrders()
                val accounts = accountRepo.getAccounts()
                val detailMap = mutableMapOf<Int, List<OrderDetail>>()
                orders.forEach { order ->
                    detailMap[order.orderId] = orderRepo.getOrderDetails(order.orderId)
                }
                Triple(orders, accounts, detailMap)
            }.onSuccess { (orders, accounts, details) ->
                _state.update {
                    it.copy(
                        orders = orders,
                        accounts = accounts,
                        orderDetails = details,
                        loading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun receiveOrder(orderId: Int, customerName: String?) {
        viewModelScope.launch {
            runCatching { orderRepo.receiveOrder(orderId) }
                .onSuccess {
                    val label = customerName?.takeIf { it.isNotBlank() } ?: "고객"
                    _state.update { state ->
                        val updated = state.orders.map { order ->
                            if (order.orderId == orderId) {
                                order.copy(orderReceived = true)
                            } else {
                                order
                            }
                        }
                        state.copy(orders = updated, toast = "${label}님의 주문이 수령 완료되었습니다!")
                    }
                    refresh()
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }
}
