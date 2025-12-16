package com.ssafy.hm.dto;

import java.util.List;

public class OrderCreateRequest {
    private String userId;
    private Integer orderStore;
    private List<OrderDetail> details;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOrderStore() {
        return orderStore;
    }

    public void setOrderStore(Integer orderStore) {
        this.orderStore = orderStore;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }
}
