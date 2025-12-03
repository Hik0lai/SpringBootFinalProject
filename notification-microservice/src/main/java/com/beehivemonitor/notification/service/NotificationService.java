package com.beehivemonitor.notification.service;

import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Notification sendEmailNotification(String recipientEmail, String subject, String message, Long alertId) {
        // Create notification record
        Notification notification = new Notification();
        notification.setRecipientEmail(recipientEmail);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setChannel(Notification.NotificationChannel.EMAIL);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notification.setAlertId(alertId);
        notification.setCreatedAt(LocalDateTime.now());
        
        notification = notificationRepository.save(notification);
        
        try {
            // Send email
            emailService.sendEmail(recipientEmail, subject, message);
            
            // Update status to SENT
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
        } catch (Exception e) {
            // Update status to FAILED
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        
        return notificationRepository.save(notification);
    }
}


