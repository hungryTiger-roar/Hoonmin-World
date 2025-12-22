package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.FcmTokenRequest;
import com.ssafy.hm.dto.NotificationRepeatRequest;
import com.ssafy.hm.dto.NotificationScheduleRequest;
import com.ssafy.hm.dto.NotificationSendRequest;
import com.ssafy.hm.dto.NotificationUpdateRequest;
import com.ssafy.hm.dto.PushNotification;

public interface NotificationService {
    void registerToken(FcmTokenRequest request);

    int sendNow(NotificationSendRequest request);

    boolean scheduleNotification(NotificationScheduleRequest request);

    boolean repeatNotification(NotificationRepeatRequest request);

    List<PushNotification> getScheduledNotifications();

    List<PushNotification> getRepeatNotifications();

    boolean updateNotification(int id, NotificationUpdateRequest request);

    boolean deleteNotification(int id);

    void processScheduledNotifications();

    void processRepeatNotifications();
}
