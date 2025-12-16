package com.ssafy.hm.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Orders {
    private Integer orderId;
    private String userId;
    private Boolean orderReceived;
    private Integer orderStore;
    private List<OrderDetail> details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderReceivedTime;

    public Orders() {}

    public Orders(Integer orderId, String userId, Boolean orderReceived,
                  Integer orderStore, LocalDateTime orderTime, LocalDateTime orderReceivedTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderReceived = orderReceived;
        this.orderStore = orderStore;
        this.orderTime = orderTime;
        this.orderReceivedTime = orderReceivedTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getOrderReceived() {
        return orderReceived;
    }

    public void setOrderReceived(Boolean orderReceived) {
        this.orderReceived = orderReceived;
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

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getOrderReceivedTime() {
        return orderReceivedTime;
    }

    public void setOrderReceivedTime(LocalDateTime orderReceivedTime) {
        this.orderReceivedTime = orderReceivedTime;
    }

    @Override
    public String toString() {
        return "Orders [orderId=" + orderId + ", userId=" + userId
                + ", orderReceived=" + orderReceived
                + ", orderStore=" + orderStore
                + ", orderTime=" + orderTime
                + ", orderReceivedTime=" + orderReceivedTime
                + ", details=" + details + "]";
    }
}
