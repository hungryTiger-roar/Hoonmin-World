package com.ssafy.hm.dto;

import java.util.List;

public class NotificationSendRequest {
    private String title;
    private String body;
    private String topic;
    private List<String> targetUserIds;

    public NotificationSendRequest() {}

    public NotificationSendRequest(String title, String body, String topic, List<String> targetUserIds) {
        this.title = title;
        this.body = body;
        this.topic = topic;
        this.targetUserIds = targetUserIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }
}
