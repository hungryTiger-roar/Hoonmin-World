package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.Orders;

@Mapper
public interface OrdersRepo {
	int insert(Orders orders);
	List<Orders> selectByUser(String userId);
	List<Orders> selectAll();
	int receiveOrder(Integer orderId);
	int delete(Integer orderId);
}
