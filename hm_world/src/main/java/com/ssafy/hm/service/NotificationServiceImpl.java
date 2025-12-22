package com.ssafy.hm.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.ssafy.hm.dto.FcmTokenRequest;
import com.ssafy.hm.dto.NotificationRepeatRequest;
import com.ssafy.hm.dto.NotificationScheduleRequest;
import com.ssafy.hm.dto.NotificationSendRequest;
import com.ssafy.hm.dto.NotificationUpdateRequest;
import com.ssafy.hm.dto.PushNotification;
import com.ssafy.hm.repo.FcmTokenRepo;
import com.ssafy.hm.repo.NotificationRepo;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final NotificationRepo notificationRepo;
    private final FcmTokenRepo fcmTokenRepo;
    private final FirebaseMessaging firebaseMessaging;

    public NotificationServiceImpl(NotificationRepo notificationRepo, FcmTokenRepo fcmTokenRepo, FirebaseMessaging firebaseMessaging) {
        this.notificationRepo = notificationRepo;
        this.fcmTokenRepo = fcmTokenRepo;
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void registerToken(FcmTokenRequest request) {
        if (request == null || !StringUtils.hasText(request.getUserId()) || !StringUtils.hasText(request.getToken())) {
            return;
        }
        fcmTokenRepo.upsert(request);
    }

    @Override
    public int sendNow(NotificationSendRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getBody())) {
            return 0;
        }

        try {
            List<String> tokens;
            if (request.getTargetUserIds() != null && !request.getTargetUserIds().isEmpty()) {
                tokens = fcmTokenRepo.selectTokensByUserIdsAndTicketTrue(request.getTargetUserIds());
            } else {
                tokens = fcmTokenRepo.selectTokensByTicketTrue();
            }
            if (tokens == null || tokens.isEmpty()) {
                return 0;
            }
            return sendToTokens(request.getTitle(), request.getBody(), tokens);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message", e);
            return 0;
        }
    }

    @Override
    public boolean scheduleNotification(NotificationScheduleRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getBody())
                || !StringUtils.hasText(request.getScheduledAt())) {
            return false;
        }
        PushNotification notification = new PushNotification();
        notification.setTitle(request.getTitle());
        notification.setBody(request.getBody());
        notification.setScheduledAt(request.getScheduledAt());
        return notificationRepo.insertScheduled(notification) == 1;
    }

    @Override
    public boolean repeatNotification(NotificationRepeatRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getBody())
                || !StringUtils.hasText(request.getRepeatDays()) || !StringUtils.hasText(request.getRepeatTime())) {
            return false;
        }
        PushNotification notification = new PushNotification();
        notification.setTitle(request.getTitle());
        notification.setBody(request.getBody());
        notification.setRepeatDays(request.getRepeatDays());
        notification.setRepeatTime(request.getRepeatTime());
        return notificationRepo.insertRepeat(notification) == 1;
    }

    @Override
    public List<PushNotification> getScheduledNotifications() {
        return notificationRepo.selectScheduled();
    }

    @Override
    public List<PushNotification> getRepeatNotifications() {
        return notificationRepo.selectRepeat();
    }

    @Override
    public boolean updateNotification(int id, NotificationUpdateRequest request) {
        PushNotification current = notificationRepo.selectById(id);
        if (current == null) {
            return false;
        }

        PushNotification update = new PushNotification();
        update.setId(id);
        update.setTitle(request.getTitle());
        update.setBody(request.getBody());
        update.setScheduledAt(request.getScheduledAt());
        update.setRepeatDays(request.getRepeatDays());
        update.setRepeatTime(request.getRepeatTime());

        if ("SCHEDULE".equalsIgnoreCase(current.getType())) {
            if (!StringUtils.hasText(update.getScheduledAt())) {
                return false;
            }
            return notificationRepo.updateScheduled(update) == 1;
        }

        if ("REPEAT".equalsIgnoreCase(current.getType())) {
            if (!StringUtils.hasText(update.getRepeatDays()) || !StringUtils.hasText(update.getRepeatTime())) {
                return false;
            }
            return notificationRepo.updateRepeat(update) == 1;
        }

        return false;
    }

    @Override
    public boolean deleteNotification(int id) {
        return notificationRepo.delete(id) == 1;
    }

    @Override
    public void processScheduledNotifications() {
        String now = LocalDateTime.now(KST).format(DATE_TIME);
        List<PushNotification> due = notificationRepo.selectDueScheduled(now);
        for (PushNotification notification : due) {
            if (!StringUtils.hasText(notification.getTitle()) || !StringUtils.hasText(notification.getBody())) {
                continue;
            }
            try {
                List<String> tokens = fcmTokenRepo.selectTokensByTicketTrue();
                if (tokens != null && !tokens.isEmpty()) {
                    sendToTokens(notification.getTitle(), notification.getBody(), tokens);
                }
                notificationRepo.markScheduledSent(notification.getId(), now);
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send scheduled notification id={}", notification.getId(), e);
            }
        }
    }

    @Override
    public void processRepeatNotifications() {
        LocalDateTime now = LocalDateTime.now(KST);
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String currentMinute = now.format(DATE_TIME);
        String dayCode = dayOfWeekCode(now.getDayOfWeek());

        List<PushNotification> repeats = notificationRepo.selectActiveRepeats();
        for (PushNotification notification : repeats) {
            if (!StringUtils.hasText(notification.getRepeatDays()) || !StringUtils.hasText(notification.getRepeatTime())) {
                continue;
            }

            if (!matchesDay(notification.getRepeatDays(), dayCode)) {
                continue;
            }

            if (!currentTime.equals(notification.getRepeatTime())) {
                continue;
            }

            if (currentMinute.equals(notification.getLastSentAt())) {
                continue;
            }

            try {
                List<String> tokens = fcmTokenRepo.selectTokensByTicketTrue();
                if (tokens != null && !tokens.isEmpty()) {
                    sendToTokens(notification.getTitle(), notification.getBody(), tokens);
                }
                notificationRepo.updateRepeatLastSent(notification.getId(), currentMinute);
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send repeat notification id={}", notification.getId(), e);
            }
        }
    }

    private void sendToTopic(String title, String body, String topic) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setTopic(topic)
            .putData("title", title)
            .putData("body", body)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();
        firebaseMessaging.send(message);
    }

    private int sendToTokens(String title, String body, List<String> tokens) throws FirebaseMessagingException {
        List<String> safeTokens = tokens == null ? new ArrayList<>() : tokens;
        if (safeTokens.isEmpty()) {
            return 0;
        }

        MulticastMessage message = MulticastMessage.builder()
            .addAllTokens(safeTokens)
            .putData("title", title)
            .putData("body", body)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();

        BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
        return response.getSuccessCount();
    }

    private boolean matchesDay(String repeatDays, String currentDay) {
        String[] parts = repeatDays.split(",");
        for (String part : parts) {
            if (part.trim().equalsIgnoreCase(currentDay)) {
                return true;
            }
        }
        return false;
    }

    private String dayOfWeekCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }
}
