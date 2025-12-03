package com.beehivemonitor.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponse {
    private Long notificationId;
    private Boolean success;
    private String message;
    private String status; // PENDING, SENT, FAILED
}


