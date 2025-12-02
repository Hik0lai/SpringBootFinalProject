package com.beehivemonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String recipientEmail;
    private String subject;
    private String message;
    private String channel; // EMAIL, SMS, PUSH
    private Long alertId; // Optional: reference to alert
}

