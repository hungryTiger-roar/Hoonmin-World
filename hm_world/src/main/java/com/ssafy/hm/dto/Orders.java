package com.ssafy.hm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Orders {
    private Integer orderId;
    private String userId;
    private Boolean orderReceived;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderReceivedTime;

    public Orders() {}

    public Orders(Integer orderId, String userId, Boolean orderReceived,
                  LocalDateTime orderTime, LocalDateTime orderReceivedTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderReceived = orderReceived;
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
                + ", orderTime=" + orderTime
                + ", orderReceivedTime=" + orderReceivedTime + "]";
    }
}
