package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.OrderDetail;

@Mapper
public interface OrderDetailRepo {
	int insert(OrderDetail detail);
	List<OrderDetail> selectByOrder(Integer orderId);
	Integer selectUnreviewedDetailId(@Param("userId") String userId, @Param("itemId") Integer itemId);
	int markReviewed(@Param("detailId") Integer detailId);
}
