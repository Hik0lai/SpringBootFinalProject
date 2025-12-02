package com.beehivemonitor.client;

import com.beehivemonitor.dto.NotificationRequest;
import com.beehivemonitor.dto.NotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for communicating with the Notification Microservice
 */
@FeignClient(name = "notification-microservice", url = "${notification.microservice.url}")
public interface NotificationMicroserviceClient {

    /**
     * Send a notification (email, SMS, push)
     * 
     * @param request Notification request with recipient, subject, message, etc.
     * @return Notification response with status and details
     */
    @PostMapping("/api/notifications/send")
    NotificationResponse sendNotification(@RequestBody NotificationRequest request);
}

