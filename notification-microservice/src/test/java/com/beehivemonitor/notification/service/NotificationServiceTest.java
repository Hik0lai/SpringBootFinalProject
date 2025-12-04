package com.beehivemonitor.notification.service;

import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for NotificationService
 * Tests notification sending and status management using Mockito
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private String recipientEmail;
    private String subject;
    private String message;
    private Long alertId;

    @BeforeEach
    void setUp() {
        recipientEmail = "test@example.com";
        subject = "Test Alert";
        message = "Temperature is above threshold";
        alertId = 1L;
    }

    @Test
    void sendEmailNotification_Success() {
        // Arrange
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setRecipientEmail(recipientEmail);
        savedNotification.setSubject(subject);
        savedNotification.setMessage(message);
        savedNotification.setChannel(Notification.NotificationChannel.EMAIL);
        savedNotification.setStatus(Notification.NotificationStatus.PENDING);
        savedNotification.setAlertId(alertId);
        savedNotification.setCreatedAt(LocalDateTime.now());

        Notification sentNotification = new Notification();
        sentNotification.setId(1L);
        sentNotification.setStatus(Notification.NotificationStatus.SENT);
        sentNotification.setSentAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification, sentNotification);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        Notification result = notificationService.sendEmailNotification(recipientEmail, subject, message, alertId);

        // Assert
        assertNotNull(result);
        assertEquals(Notification.NotificationStatus.SENT, result.getStatus());
        assertNotNull(result.getSentAt());
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail(recipientEmail, subject, message);
    }

    @Test
    void sendEmailNotification_EmailServiceThrowsException_SetsStatusToFailed() {
        // Arrange
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setRecipientEmail(recipientEmail);
        savedNotification.setSubject(subject);
        savedNotification.setMessage(message);
        savedNotification.setChannel(Notification.NotificationChannel.EMAIL);
        savedNotification.setStatus(Notification.NotificationStatus.PENDING);
        savedNotification.setAlertId(alertId);
        savedNotification.setCreatedAt(LocalDateTime.now());

        Notification failedNotification = new Notification();
        failedNotification.setId(1L);
        failedNotification.setStatus(Notification.NotificationStatus.FAILED);
        failedNotification.setErrorMessage("Mail server error");

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification, failedNotification);
        doThrow(new RuntimeException("Mail server error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        Notification result = notificationService.sendEmailNotification(recipientEmail, subject, message, alertId);

        // Assert
        assertNotNull(result);
        assertEquals(Notification.NotificationStatus.FAILED, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Mail server error"));
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail(recipientEmail, subject, message);
    }

    @Test
    void sendEmailNotification_NullAlertId_Success() {
        // Arrange
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setRecipientEmail(recipientEmail);
        savedNotification.setSubject(subject);
        savedNotification.setMessage(message);
        savedNotification.setChannel(Notification.NotificationChannel.EMAIL);
        savedNotification.setStatus(Notification.NotificationStatus.PENDING);
        savedNotification.setAlertId(null);

        Notification sentNotification = new Notification();
        sentNotification.setId(1L);
        sentNotification.setStatus(Notification.NotificationStatus.SENT);
        sentNotification.setSentAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification, sentNotification);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        Notification result = notificationService.sendEmailNotification(recipientEmail, subject, message, null);

        // Assert
        assertNotNull(result);
        assertNull(result.getAlertId());
        assertEquals(Notification.NotificationStatus.SENT, result.getStatus());
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail(recipientEmail, subject, message);
    }

    @Test
    void sendEmailNotification_EmptyMessage_Success() {
        // Arrange
        String emptyMessage = "";
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setMessage(emptyMessage);
        savedNotification.setStatus(Notification.NotificationStatus.PENDING);

        Notification sentNotification = new Notification();
        sentNotification.setId(1L);
        sentNotification.setStatus(Notification.NotificationStatus.SENT);
        sentNotification.setSentAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification, sentNotification);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        Notification result = notificationService.sendEmailNotification(recipientEmail, subject, emptyMessage, alertId);

        // Assert
        assertNotNull(result);
        assertEquals(Notification.NotificationStatus.SENT, result.getStatus());
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail(recipientEmail, subject, emptyMessage);
    }

    @Test
    void sendEmailNotification_CreatesPendingNotificationFirst() {
        // Arrange
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setStatus(Notification.NotificationStatus.PENDING);

        Notification sentNotification = new Notification();
        sentNotification.setId(1L);
        sentNotification.setStatus(Notification.NotificationStatus.SENT);

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notif = invocation.getArgument(0);
                    if (notif.getStatus() == Notification.NotificationStatus.PENDING) {
                        return savedNotification;
                    } else {
                        return sentNotification;
                    }
                });
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        Notification result = notificationService.sendEmailNotification(recipientEmail, subject, message, alertId);

        // Assert
        assertNotNull(result);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        // Verify that the first save has PENDING status
        verify(notificationRepository).save(argThat(notif -> 
            notif.getStatus() == Notification.NotificationStatus.PENDING &&
            notif.getChannel() == Notification.NotificationChannel.EMAIL &&
            notif.getRecipientEmail().equals(recipientEmail)
        ));
    }
}

