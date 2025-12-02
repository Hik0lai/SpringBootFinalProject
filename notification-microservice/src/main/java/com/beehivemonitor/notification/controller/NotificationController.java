package com.beehivemonitor.notification.controller;

import com.beehivemonitor.notification.dto.SendNotificationRequest;
import com.beehivemonitor.notification.dto.SendNotificationResponse;
import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"})
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<SendNotificationResponse> sendNotification(@RequestBody SendNotificationRequest request) {
        try {
            Notification notification;
            
            if ("EMAIL".equalsIgnoreCase(request.getChannel())) {
                notification = notificationService.sendEmailNotification(
                    request.getRecipientEmail(),
                    request.getSubject(),
                    request.getMessage(),
                    request.getAlertId()
                );
            } else {
                return ResponseEntity.badRequest().body(
                    new SendNotificationResponse(null, false, "Unsupported channel: " + request.getChannel(), "FAILED")
                );
            }
            
            return ResponseEntity.ok(new SendNotificationResponse(
                notification.getId(),
                notification.getStatus() == Notification.NotificationStatus.SENT,
                notification.getStatus() == Notification.NotificationStatus.SENT ? "Notification sent successfully" : "Failed to send notification",
                notification.getStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new SendNotificationResponse(null, false, "Error: " + e.getMessage(), "FAILED")
            );
        }
    }
}

