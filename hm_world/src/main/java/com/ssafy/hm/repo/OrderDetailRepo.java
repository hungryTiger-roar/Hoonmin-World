package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.OrderDetail;

@Mapper
public interface OrderDetailRepo {
	int insert(OrderDetail detail);
	List<OrderDetail> selectByOrder(Integer orderId);
}
