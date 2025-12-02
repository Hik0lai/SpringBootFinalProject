package com.beehivemonitor.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    private String recipientEmail;
    private String subject;
    private String message;
    private String channel; // EMAIL, SMS, PUSH
    private Long alertId; // Optional: reference to alert
}

