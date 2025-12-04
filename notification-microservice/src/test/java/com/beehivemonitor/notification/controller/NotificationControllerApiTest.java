package com.beehivemonitor.notification.controller;

import com.beehivemonitor.notification.dto.SendNotificationRequest;
import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for NotificationController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private SendNotificationRequest validRequest;
    private Notification successNotification;
    private Notification failedNotification;

    @BeforeEach
    void setUp() {
        validRequest = new SendNotificationRequest();
        validRequest.setRecipientEmail("test@example.com");
        validRequest.setSubject("Test Alert");
        validRequest.setMessage("Temperature is above threshold");
        validRequest.setChannel("EMAIL");
        validRequest.setAlertId(1L);

        successNotification = new Notification();
        successNotification.setId(1L);
        successNotification.setRecipientEmail("test@example.com");
        successNotification.setSubject("Test Alert");
        successNotification.setMessage("Temperature is above threshold");
        successNotification.setChannel(Notification.NotificationChannel.EMAIL);
        successNotification.setStatus(Notification.NotificationStatus.SENT);
        successNotification.setAlertId(1L);
        successNotification.setCreatedAt(LocalDateTime.now());
        successNotification.setSentAt(LocalDateTime.now());

        failedNotification = new Notification();
        failedNotification.setId(2L);
        failedNotification.setRecipientEmail("test@example.com");
        failedNotification.setSubject("Test Alert");
        failedNotification.setMessage("Temperature is above threshold");
        failedNotification.setChannel(Notification.NotificationChannel.EMAIL);
        failedNotification.setStatus(Notification.NotificationStatus.FAILED);
        failedNotification.setAlertId(1L);
        failedNotification.setCreatedAt(LocalDateTime.now());
        failedNotification.setErrorMessage("Mail server error");
    }

    @Test
    void testSendNotification_EmailChannel_Success() throws Exception {
        // Arrange
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.notificationId").value(1L))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void testSendNotification_EmailChannelFailed_ReturnsSuccessWithFailedStatus() throws Exception {
        // Arrange
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(failedNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.notificationId").value(2L))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to send notification"))
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void testSendNotification_UnsupportedChannel_ReturnsBadRequest() throws Exception {
        // Arrange
        validRequest.setChannel("SMS");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unsupported channel: SMS"))
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void testSendNotification_InvalidChannel_ReturnsBadRequest() throws Exception {
        // Arrange
        validRequest.setChannel("INVALID");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported channel: INVALID"));
    }

    @Test
    void testSendNotification_EmailServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Mail server connection failed"));

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error: Mail server connection failed"))
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void testSendNotification_NullAlertId_Success() throws Exception {
        // Arrange
        validRequest.setAlertId(null);
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_EmptyMessage_Success() throws Exception {
        // Arrange
        validRequest.setMessage("");
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_MissingFields_ReturnsBadRequest() throws Exception {
        // Arrange
        SendNotificationRequest invalidRequest = new SendNotificationRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_InvalidJson_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_CaseInsensitiveChannel_Success() throws Exception {
        // Arrange - Test case insensitive channel
        validRequest.setChannel("email"); // lowercase
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_MixedCaseChannel_Success() throws Exception {
        // Arrange - Test mixed case channel
        validRequest.setChannel("Email"); // mixed case
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_PendingStatus_ReturnsPendingStatus() throws Exception {
        // Arrange
        Notification pendingNotification = new Notification();
        pendingNotification.setId(3L);
        pendingNotification.setStatus(Notification.NotificationStatus.PENDING);
        pendingNotification.setChannel(Notification.NotificationChannel.EMAIL);

        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(pendingNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to send notification"));
    }

    @Test
    void testSendNotification_LongMessage_Success() throws Exception {
        // Arrange
        String longMessage = "A".repeat(10000);
        validRequest.setMessage(longMessage);
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_SpecialCharactersInSubject_Success() throws Exception {
        // Arrange
        validRequest.setSubject("Alert: Temperature > 30°C & Humidity < 50%");
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_NullChannel_ReturnsBadRequest() throws Exception {
        // Arrange
        validRequest.setChannel(null);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported channel: null"));
    }

    @Test
    void testSendNotification_EmptyChannel_ReturnsBadRequest() throws Exception {
        // Arrange
        validRequest.setChannel("");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported channel: "));
    }

    @Test
    void testSendNotification_PushChannel_ReturnsBadRequest() throws Exception {
        // Arrange
        validRequest.setChannel("PUSH");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported channel: PUSH"));
    }

    @Test
    void testSendNotification_EmptySubject_Success() throws Exception {
        // Arrange
        validRequest.setSubject("");
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSendNotification_NullRecipientEmail_ReturnsInternalServerError() throws Exception {
        // Arrange
        validRequest.setRecipientEmail(null);
        // When recipientEmail is null, the service will throw an exception
        // which causes the controller to return 500 Internal Server Error
        
        // Act & Assert - Null email causes service failure, controller returns 500
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void testSendNotification_MissingContentType_ReturnsUnsupportedMediaType() throws Exception {
        // Act & Assert
        // Spring returns 415 Unsupported Media Type for missing/wrong Content-Type header
        mockMvc.perform(post("/api/notifications/send")
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testSendNotification_EmptyRequestBody_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_DifferentExceptionTypes_ReturnsInternalServerError() throws Exception {
        // Arrange - Test different exception types
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("Invalid email format"));

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error: Invalid email format"));
    }

    @Test
    void testSendNotification_ResponseContainsCorrectFields() throws Exception {
        // Arrange
        when(notificationService.sendEmailNotification(
                anyString(), anyString(), anyString(), any()))
                .thenReturn(successNotification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").exists())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").exists());
    }
}

