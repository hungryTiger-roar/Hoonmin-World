package com.ssafy.hm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.hm.dto.FcmTokenRequest;
import com.ssafy.hm.dto.NotificationRepeatRequest;
import com.ssafy.hm.dto.NotificationScheduleRequest;
import com.ssafy.hm.dto.NotificationSendRequest;
import com.ssafy.hm.dto.NotificationUpdateRequest;
import com.ssafy.hm.dto.PushNotification;
import com.ssafy.hm.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notifications")
@Tag(name = "notifications", description = "FCM push notification management")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/token")
    @Operation(summary = "Register FCM token")
    public ResponseEntity<Void> registerToken(@RequestBody FcmTokenRequest request) {
        notificationService.registerToken(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    @Operation(summary = "Send push notification now")
    public ResponseEntity<Map<String, Object>> sendNow(@RequestBody NotificationSendRequest request) {
        int successCount = notificationService.sendNow(request);
        Map<String, Object> body = new HashMap<>();
        body.put("successCount", successCount);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/schedule")
    @Operation(summary = "Schedule push notification")
    public ResponseEntity<Void> schedule(@RequestBody NotificationScheduleRequest request) {
        boolean ok = notificationService.scheduleNotification(request);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/repeat")
    @Operation(summary = "Create repeating push notification")
    public ResponseEntity<Void> repeat(@RequestBody NotificationRepeatRequest request) {
        boolean ok = notificationService.repeatNotification(request);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/scheduled")
    @Operation(summary = "List scheduled notifications")
    public ResponseEntity<List<PushNotification>> scheduledList() {
        return ResponseEntity.ok(notificationService.getScheduledNotifications());
    }

    @GetMapping("/repeat")
    @Operation(summary = "List repeating notifications")
    public ResponseEntity<List<PushNotification>> repeatList() {
        return ResponseEntity.ok(notificationService.getRepeatNotifications());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update scheduled/repeating notification")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody NotificationUpdateRequest request) {
        boolean ok = notificationService.updateNotification(id, request);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete scheduled/repeating notification")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean ok = notificationService.deleteNotification(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
