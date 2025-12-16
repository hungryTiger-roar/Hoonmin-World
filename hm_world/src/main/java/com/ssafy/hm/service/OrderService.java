package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.OrderCreateRequest;
import com.ssafy.hm.dto.OrderDetail;
import com.ssafy.hm.dto.Orders;

public interface OrderService {
	Integer createOrder(OrderCreateRequest request);
	boolean addDetail(OrderDetail detail);
	List<Orders> findOrders(String userId);
	List<Orders> findAllOrders();
	List<OrderDetail> findDetails(Integer orderId);
	boolean receiveOrder(Integer orderId);
	boolean deleteOrder(Integer orderId);
}
