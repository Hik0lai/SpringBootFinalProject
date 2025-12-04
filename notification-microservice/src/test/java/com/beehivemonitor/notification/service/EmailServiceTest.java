package com.beehivemonitor.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for EmailService
 * Tests email sending functionality using Mockito
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private String fromEmail;
    private String fromName;

    @BeforeEach
    void setUp() {
        fromEmail = "test@beehivemonitor.com";
        fromName = "Beehive Monitor";
        
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
        ReflectionTestUtils.setField(emailService, "fromName", fromName);
    }

    @Test
    void sendEmail_Success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, message));

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_SetsCorrectProperties() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        
        doAnswer(invocation -> {
            SimpleMailMessage email = invocation.getArgument(0);
            assertEquals(fromEmail, email.getFrom());
            assertEquals(to, email.getTo()[0]);
            assertEquals(subject, email.getSubject());
            assertEquals(message, email.getText());
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, message);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_MailSenderThrowsException_ThrowsRuntimeException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        
        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail(to, subject, message);
        });

        assertTrue(exception.getMessage().contains("Failed to send email"));
        assertTrue(exception.getMessage().contains("Mail server error"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_EmptyMessage_Success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String message = "";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, message));

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_LongMessage_Success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String message = "A".repeat(10000); // Very long message
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, message));

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_SpecialCharactersInSubject_Success() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Alert: Temperature > 30°C & Humidity < 50%";
        String message = "Test Message";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, message));

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

