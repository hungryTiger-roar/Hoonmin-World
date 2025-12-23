package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.OrderCreateRequest;
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
	public Integer createOrder(OrderCreateRequest request) {
		Orders orders = new Orders();
		orders.setUserId(request.getUserId());
		orders.setOrderStore(request.getOrderStore());
		int result = ordersRepo.insert(orders);
		if (result != 1) {
			return null;
		}

		// 주문 상세 일괄 생성
		List<OrderDetail> details = request.getDetails();
		if (details != null) {
			for (OrderDetail detail : details) {
				detail.setOrderId(orders.getOrderId());
				int inserted = detailRepo.insert(detail);
				if (inserted != 1) {
					throw new IllegalStateException("Failed to insert order detail");
				}
			}
		}
		return orders.getOrderId();
	}

	@Override
	@Transactional
	public boolean addDetail(OrderDetail detail) {
		return detailRepo.insert(detail) == 1;
	}

	@Override
	public List<Orders> findOrders(String userId) {
		List<Orders> orders = ordersRepo.selectByUser(userId);
		for (Orders o : orders) {
			o.setDetails(detailRepo.selectByOrder(o.getOrderId()));
		}
		return orders;
	}

	@Override
	public List<OrderDetail> findDetails(Integer orderId) {
		return detailRepo.selectByOrder(orderId);
	}
	
	@Override
	public List<Orders> findAllOrders() {
		List<Orders> orders = ordersRepo.selectAll();
		for (Orders o : orders) {
			o.setDetails(detailRepo.selectByOrder(o.getOrderId()));
		}
		return orders;
	}

	@Override
	@Transactional
	public boolean receiveOrder(Integer orderId) {
		int updated = ordersRepo.receiveOrder(orderId);
		if (updated == 1) {
			return true;
		}
		Orders existing = ordersRepo.selectById(orderId);
		return existing != null && Boolean.TRUE.equals(existing.getOrderReceived());
	}
	
	@Override
	@Transactional
	public boolean deleteOrder(Integer orderId) {
		// order_detail는 FK ON DELETE CASCADE 설정, 주문만 삭제해도 연쇄 삭제됨
		return ordersRepo.delete(orderId) == 1;
	}
}
