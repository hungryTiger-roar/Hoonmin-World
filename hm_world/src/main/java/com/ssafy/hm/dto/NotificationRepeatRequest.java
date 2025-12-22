package com.ssafy.hm.dto;

public class NotificationRepeatRequest {
    private String title;
    private String body;
    private String repeatDays;
    private String repeatTime;

    public NotificationRepeatRequest() {}

    public NotificationRepeatRequest(String title, String body, String repeatDays, String repeatTime) {
        this.title = title;
        this.body = body;
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
