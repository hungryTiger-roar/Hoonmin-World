package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.OrderDetail;
import com.ssafy.hm.dto.Orders;
import com.ssafy.hm.repo.OrderDetailRepo;
import com.ssafy.hm.repo.OrdersRepo;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrdersRepo ordersRepo;
	private final OrderDetailRepo detailRepo;

	public OrderServiceImpl(OrdersRepo ordersRepo, OrderDetailRepo detailRepo) {
		this.ordersRepo = ordersRepo;
		this.detailRepo = detailRepo;
	}

	@Override
	@Transactional
	public Integer createOrder(Orders orders) {
		int result = ordersRepo.insert(orders);
		if (result == 1) {
			return orders.getOrderId();
		}
		return null;
	}

	@Override
	@Transactional
	public boolean addDetail(OrderDetail detail) {
		return detailRepo.insert(detail) == 1;
	}

	@Override
	public List<Orders> findOrders(String userId) {
		return ordersRepo.selectByUser(userId);
	}

	@Override
	public List<OrderDetail> findDetails(Integer orderId) {
		return detailRepo.selectByOrder(orderId);
	}
}
