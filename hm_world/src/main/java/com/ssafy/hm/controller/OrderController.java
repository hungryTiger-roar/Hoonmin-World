package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.hm.dto.OrderDetail;
import com.ssafy.hm.dto.Orders;
import com.ssafy.hm.service.OrderService;

@RestController
@RequestMapping("/orders")
@Tag(name = "주문", description = "주문 생성 및 상세 관리")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@Operation(summary = "주문 생성")
	public ResponseEntity<?> createOrder(@RequestBody Orders orders) {
		Integer orderId = orderService.createOrder(orders);
		return orderId != null ? ResponseEntity.ok(orderId) : ResponseEntity.badRequest().build();
	}

	@PostMapping("/{orderId}/details")
	@Operation(summary = "주문 상세 추가")
	public ResponseEntity<?> addDetail(@PathVariable Integer orderId, @RequestBody OrderDetail detail) {
		detail.setOrderId(orderId);
		boolean created = orderService.addDetail(detail);
		return created ? ResponseEntity.ok(detail) : ResponseEntity.badRequest().build();
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "사용자별 주문 목록 조회")
	public ResponseEntity<List<Orders>> getOrders(@PathVariable String userId) {
		return ResponseEntity.ok(orderService.findOrders(userId));
	}

	@GetMapping("/{orderId}/details")
	@Operation(summary = "주문 상세 조회")
	public ResponseEntity<List<OrderDetail>> getDetails(@PathVariable Integer orderId) {
		return ResponseEntity.ok(orderService.findDetails(orderId));
	}
}
