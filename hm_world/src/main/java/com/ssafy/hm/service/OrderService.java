package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.OrderDetail;
import com.ssafy.hm.dto.Orders;

public interface OrderService {
	Integer createOrder(Orders orders);
	boolean addDetail(OrderDetail detail);
	List<Orders> findOrders(String userId);
	List<OrderDetail> findDetails(Integer orderId);
}
