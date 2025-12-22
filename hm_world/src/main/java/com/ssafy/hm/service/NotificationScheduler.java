package com.ssafy.hm.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 60000)
    public void run() {
        notificationService.processScheduledNotifications();
        notificationService.processRepeatNotifications();
    }
}
