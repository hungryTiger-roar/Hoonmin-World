package com.ssafy.hm.dto;

public class NotificationUpdateRequest {
    private String title;
    private String body;
    private String scheduledAt;
    private String repeatDays;
    private String repeatTime;

    public NotificationUpdateRequest() {}

    public NotificationUpdateRequest(String title, String body, String scheduledAt, String repeatDays, String repeatTime) {
        this.title = title;
        this.body = body;
        this.scheduledAt = scheduledAt;
        this.repeatDays = repeatDays;
        this.repeatTime = repeatTime;
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

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(String repeatTime) {
        this.repeatTime = repeatTime;
    }
}
