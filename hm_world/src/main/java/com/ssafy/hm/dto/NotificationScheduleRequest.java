package com.ssafy.hm.dto;

public class NotificationScheduleRequest {
    private String title;
    private String body;
    private String scheduledAt;

    public NotificationScheduleRequest() {}

    public NotificationScheduleRequest(String title, String body, String scheduledAt) {
        this.title = title;
        this.body = body;
        this.scheduledAt = scheduledAt;
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

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
